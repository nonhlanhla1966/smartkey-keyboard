package com.smartkey.keyboard

import android.content.Context
import android.content.ClipboardManager
import android.content.ClipData

class SmartClipboard(context: Context, prefs: SmartPrefs) {
    private val ctx = context.applicationContext
    private val prefs = prefs
    private val key = "smartkey-clip"
    private var lastAutosaved = 0L

    fun isEnabled(): Boolean = prefs.getBoolean(SmartPrefs.KEY_CLIPBOARD_ENABLED, false)

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

    fun push(text: String) {
        if (!isEnabled()) return
        val list = items().toMutableList()
        val existing = list.indexOfFirst { it.text == text }
        if (existing >= 0) list.removeAt(existing)
        list.add(0, ClipItem(text, System.currentTimeMillis()))
        while (list.size > 40) list.removeAt(list.size - 1)
        save(list)
    }

    fun items(): List<ClipItem> {
        val raw = prefs.getString(SmartPrefs.KEY_CLIPBOARD_ENABLED + "_list") ?: return emptyList()
        val decrypted = XORCipher.decrypt(raw, key)
        val out = ArrayList<ClipItem>()
        for (part in decrypted.split("\n")) {
            if (part.isBlank()) continue
            val idx = part.lastIndexOf(':')
            if (idx > 0) {
                val t = part.substring(0, idx)
                val ts = part.substring(idx + 1).toLongOrNull() ?: System.currentTimeMillis()
                if (t.isNotBlank()) out.add(ClipItem(t, ts))
            }
        }
        return prune(out)
    }

    private fun prune(list: List<ClipItem>): List<ClipItem> {
        val hours = prefs.getInt(SmartPrefs.KEY_CLIPBOARD_HOURS, 12).toLong()
        val cutoff = System.currentTimeMillis() - hours * 3600_000L
        return list.filter { it.timestamp >= cutoff }
    }

    fun setItems(list: List<ClipItem>) {
        val sb = StringBuilder()
        for (item in list) {
            sb.append(item.text.replace('\n', ' ')).append(':').append(item.timestamp).append('\n')
        }
        prefs.putString(SmartPrefs.KEY_CLIPBOARD_ENABLED + "_list", XORCipher.encrypt(sb.toString(), key))
    }

    private fun save(list: List<ClipItem>) {
        val sb = StringBuilder()
        for (item in list) {
            sb.append(item.text.replace('\n', ' ')).append(':').append(item.timestamp).append('\n')
        }
        prefs.putString(SmartPrefs.KEY_CLIPBOARD_ENABLED + "_list", XORCipher.encrypt(sb.toString(), key))
    }

    fun delete(item: ClipItem) {
        val list = items().toMutableList()
        list.remove(item)
        save(list)
    }

    fun clearAll() {
        prefs.remove(SmartPrefs.KEY_CLIPBOARD_ENABLED + "_list")
    }

    data class ClipItem(val text: String, val timestamp: Long)
}