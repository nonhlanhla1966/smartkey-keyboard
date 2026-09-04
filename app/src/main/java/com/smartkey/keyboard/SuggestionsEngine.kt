package com.smartkey.keyboard

import android.content.Context

class SmartPrefs(context: Context) {
    val prefs = context.getSharedPreferences("smartkey", Context.MODE_PRIVATE)

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? = prefs.getString(key, null)

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, def: Int): Int = prefs.getInt(key, def)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, def: Boolean): Boolean = prefs.getBoolean(key, def)

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    companion object {
        const val KEY_THEME = "theme"
        const val KEY_HAPTICS = "haptics"
        const val KEY_SOUNDS = "sounds"
        const val KEY_SUGGESTIONS = "suggestions"
        const val KEY_AUTOCAP = "autocap"
        const val KEY_LEARNING = "learning"
        const val KEY_CLIPBOARD_ENABLED = "clipboard_enabled"
        const val KEY_CLIPBOARD_HOURS = "clipboard_hours"
        const val KEY_LEARNED_WORDS = "learned_words"
    }
}

class SuggestionsEngine(private val prefs: SmartPrefs) {
    private val learned = HashMap<String, Int>()

    private val COMMON = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "I",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "hello", "thanks", "please", "okay", "sorry", "right", "great", "home", "love", "friend"
    )

    init {
        loadLearned()
    }

    private fun wordKey(word: String): String = word.trim().lowercase()

    fun loadLearned() {
        learned.clear()
        val raw = prefs.getString(SmartPrefs.KEY_LEARNED_WORDS) ?: return
        val serialized = XORCipher.decrypt(raw, "smartkey-learn")
        for (part in serialized.split("\uE000")) {
            val idx = part.lastIndexOf(':')
            if (idx > 0) {
                val w = part.substring(0, idx).trim()
                val c = part.substring(idx + 1).toIntOrNull() ?: 0
                if (w.isNotEmpty()) learned[w] = c
            }
        }
    }

    fun saveLearned() {
        val sb = StringBuilder()
        for ((w, c) in learned) {
            if (sb.isNotEmpty()) sb.append('\uE000')
            sb.append(w).append(':').append(c)
        }
        prefs.putString(SmartPrefs.KEY_LEARNED_WORDS, XORCipher.encrypt(sb.toString(), "smartkey-learn"))
    }

    fun learn(word: String) {
        val w = wordKey(word)
        if (w.isEmpty() || w.length < 2) return
        val cur = learned[w] ?: 0
        learned[w] = cur + 1
        if (learned.size > 350) {
            val min = learned.values.minOrNull() ?: 0
            val it = learned.entries.iterator()
            while (it.hasNext()) {
                if (it.next().value <= min) it.remove()
            }
        }
        saveLearned()
    }

    fun clear() {
        learned.clear()
        prefs.remove(SmartPrefs.KEY_LEARNED_WORDS)
    }

    fun suggest(prefixRaw: String, count: Int = 3): List<String> {
        val prefix = wordKey(prefixRaw)
        if (prefix.isEmpty()) return emptyList()
        val pool = HashMap<String, Int>()
        if (prefs.getBoolean(SmartPrefs.KEY_LEARNING, true)) {
            for ((w, c) in learned) {
                if (w.startsWith(prefix)) pool[w] = c
            }
        }
        for ((i, w) in COMMON.withIndex()) {
            if (w.startsWith(prefix)) {
                val existing = pool[w] ?: 0
                val bonus = (COMMON.size - i)
                pool[w] = existing + bonus
            }
        }
        val ranked = pool.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key.length })
            .map { it.key }
        val out = ArrayList<String>()
        for (w in ranked) {
            if (w == prefixRaw.trim().lowercase()) continue
            out.add(w)
            if (out.size >= count) break
        }
        return out
    }

    fun shouldCapitalize(prefix: String): Boolean {
        val p = prefix.trim()
        if (p.isEmpty()) return true
        if (p.endsWith(". ") || p.endsWith("! ") || p.endsWith("? ")) return true
        if (p.endsWith(".\n") || p.endsWith("!\n") || p.endsWith("?\n")) return true
        if (p.endsWith("\n")) return true
        return false
    }
}

object XORCipher {
    fun encrypt(s: String, key: String): String {
        val k = key.toByteArray()
        val bytes = s.toByteArray(Charsets.UTF_8)
        val out = ByteArray(bytes.size)
        for (i in bytes.indices) out[i] = (bytes[i].toInt() xor k[i % k.size].toInt()).toByte()
        return android.util.Base64.encodeToString(out, android.util.Base64.NO_WRAP)
    }

    fun decrypt(s: String, key: String): String {
        return try {
            val k = key.toByteArray()
            val bytes = android.util.Base64.decode(s, android.util.Base64.NO_WRAP)
            val out = ByteArray(bytes.size)
            for (i in bytes.indices) out[i] = (bytes[i].toInt() xor k[i % k.size].toInt()).toByte()
            String(out, Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }
}