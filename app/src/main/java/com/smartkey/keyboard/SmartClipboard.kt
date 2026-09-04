package com.smartkey.keyboard

import android.content.Context
import android.content.ClipboardManager
import android.content.ClipData

class SmartClipboard(context: Context, prefs: SmartPrefs) {
    private val ctx = context.applicationContext
    private val prefs = prefs
    private val key = "smartkey-clip"
    private var lastAutosaved = 0L
    private var unlocked = false

    data class ClipItem(
        val text: String,
        val timestamp: Long,
        val pinned: Boolean = false,
        val name: String? = null
    ) {
        val displayName: String get() = name ?: text
    }

    fun isEnabled(): Boolean = prefs.getBoolean(SmartPrefs.KEY_CLIPBOARD_ENABLED, false)

    // ---- Password protection ----

    fun hasPassword(): Boolean {
        val pin = prefs.getString(SmartPrefs.KEY_CLIPBOARD_PIN) ?: ""
        return pin.isNotEmpty()
    }

    fun setPassword(pin: String) {
        if (pin.isEmpty()) {
            prefs.remove(SmartPrefs.KEY_CLIPBOARD_PIN)
            unlocked = true
        } else {
            prefs.putString(SmartPrefs.KEY_CLIPBOARD_PIN, secureHash(pin))
            unlocked = true
        }
    }

    fun verifyPin(pin: String): Boolean {
        val expected = prefs.getString(SmartPrefs.KEY_CLIPBOARD_PIN) ?: ""
        if (expected.isEmpty()) return true
        unlocked = secureHash(pin) == expected
        return unlocked
    }

    fun lock() {
        unlocked = false
    }

    fun isUnlocked(): Boolean = unlocked || !hasPassword()

    private fun secureHash(s: String): String {
        if (s.isEmpty()) return ""
        return try {
            val md = java.security.MessageDigest.getInstance("SHA-256")
            val bytes = md.digest(("smartkey-pin-salt" + s).toByteArray(Charsets.UTF_8))
            val sb = StringBuilder(bytes.size * 2)
            for (b in bytes) sb.append(String.format(java.util.Locale.US, "%02x", b.toInt() and 0xFF))
            sb.toString()
        } catch (e: Exception) {
            ""
        }
    }

    // ---- System clipboard bridge ----

    fun systemText(): String? {
        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = cm.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        val item = clip.getItemAt(0)
        val text = item.coerceToText(ctx)?.toString() ?: return null
        if (text.isBlank()) return null
        if (text.length > 5000) return text.take(5000)
        return text
    }

    fun copy(text: String): Boolean {
        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        try {
            cm.setPrimaryClip(ClipData.newPlainText("smartkey", text))
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun capture() {
        if (!isEnabled()) return
        val text = systemText() ?: return
        val now = System.currentTimeMillis()
        if (now - lastAutosaved < 2500) return
        lastAutosaved = now
        push(text)
    }

    // ---- History storage (delegates to ClipboardStore) ----

    fun push(text: String) {
        if (!isEnabled()) return
        val items = storeItems().toMutableList()
        val updated = ClipboardStore.push(items, text, System.currentTimeMillis())
        save(updated)
    }

    fun items(): List<ClipItem> =
        ClipboardStore.sorted(rawStoreItems()).map { toItem(it) }

    fun search(query: String): List<ClipItem> {
        return ClipboardStore.search(rawStoreItems(), query).map { toItem(it) }
    }

    fun togglePin(item: ClipItem) {
        save(ClipboardStore.togglePin(rawStoreItems(), item.text))
    }

    fun rename(item: ClipItem, newName: String) {
        save(ClipboardStore.rename(rawStoreItems(), item.text, newName.orEmpty()))
    }

    fun delete(item: ClipItem) {
        save(ClipboardStore.delete(rawStoreItems(), item.text))
    }

    fun clearAll() {
        prefs.remove(SmartPrefs.KEY_CLIPBOARD_ENABLED + "_list")
    }

    private fun toItem(it: ClipboardStore.Item) = ClipItem(it.text, it.timestamp, it.pinned, it.name)

    private fun storeItems(): List<ClipboardStore.Item> = rawStoreItems()

    private fun rawStoreItems(): List<ClipboardStore.Item> {
        val raw = prefs.getString(SmartPrefs.KEY_CLIPBOARD_ENABLED + "_list") ?: return emptyList()
        val decrypted = XORCipher.decrypt(raw, key)
        val parsed = ClipboardStore.parse(decrypted)
        val now = System.currentTimeMillis()
        val hours = prefs.getInt(SmartPrefs.KEY_CLIPBOARD_HOURS, 12).toLong()
        return ClipboardStore.sorted(ClipboardStore.prune(parsed, hours, now))
    }

    private fun save(list: List<ClipboardStore.Item>) {
        val serialized = ClipboardStore.serialize(ClipboardStore.sorted(list))
        val encrypted = XORCipher.encrypt(serialized, key)
        if (encrypted.isEmpty()) {
            prefs.remove(SmartPrefs.KEY_CLIPBOARD_ENABLED + "_list")
        } else {
            prefs.putString(SmartPrefs.KEY_CLIPBOARD_ENABLED + "_list", encrypted)
        }
    }
}