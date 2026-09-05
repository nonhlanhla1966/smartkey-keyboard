package com.smartkey.keyboard

/**
 * User-defined text shortcuts (snippets), e.g. "brb" -> "be right back".
 * Stored encrypted on-device, exactly like learned words.
 */
class TextShortcuts(private val prefs: SmartPrefs) {

    private val map = HashMap<String, String>()

    init {
        load()
    }

    fun load() {
        map.clear()
        val raw = prefs.getString(SmartPrefs.KEY_SHORTCUTS) ?: return
        val serialized = XORCipher.decrypt(raw, "smartkey-shortcuts")
        for (part in serialized.split('\uE000')) {
            val idx = part.lastIndexOf('\uE001')
            if (idx > 0) {
                val shortcut = part.substring(0, idx).trim().lowercase()
                val replacement = part.substring(idx + 1)
                if (shortcut.isNotEmpty() && replacement.isNotEmpty()) {
                    map[shortcut] = replacement
                }
            }
        }
    }

    fun save() {
        val sb = StringBuilder()
        for ((shortcut, replacement) in map) {
            if (sb.isNotEmpty()) sb.append('\uE000')
            sb.append(shortcut).append('\uE001').append(replacement)
        }
        prefs.putString(SmartPrefs.KEY_SHORTCUTS, XORCipher.encrypt(sb.toString(), "smartkey-shortcuts"))
    }

    fun all(): Map<String, String> = LinkedHashMap(map.toSortedMap())

    fun put(shortcut: String, replacement: String): Boolean {
        val key = shortcut.trim().lowercase()
        val value = replacement.trim()
        if (key.isEmpty() || value.isEmpty()) return false
        if (key.length > 24) return false
        if (value.length > 500) return false
        map[key] = value
        save()
        return true
    }

    fun remove(shortcut: String) {
        map.remove(shortcut.trim().lowercase())
        save()
    }

    fun expand(word: String): String? {
        val key = word.trim().lowercase()
        if (key.isEmpty()) return null
        return map[key]
    }
}