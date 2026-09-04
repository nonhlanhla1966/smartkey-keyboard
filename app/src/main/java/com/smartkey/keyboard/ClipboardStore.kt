package com.smartkey.keyboard

/**
 * Pure storage/serialization logic for clipboard history. No Android
 * dependencies so it can be unit tested on the JVM.
 */
object ClipboardStore {

    data class Item(
        val text: String,
        val timestamp: Long,
        val pinned: Boolean = false,
        val name: String? = null
    ) {
        val displayName: String get() = name ?: text
    }

    const val MAX_ITEMS = 40
    private const val FIELD_SEP = '\uE001'

    /** Serialize items into an encrypted-storable string. Returns null when empty. */
    fun serialize(items: List<Item>): String {
        if (items.isEmpty()) return ""
        val sb = StringBuilder()
        for (item in items) {
            sb.append(item.text.replace('\n', ' '))
            sb.append(FIELD_SEP).append(item.timestamp)
            sb.append(FIELD_SEP).append(if (item.pinned) "1" else "0")
            sb.append(FIELD_SEP).append(item.name ?: "")
            sb.append('\n')
        }
        return sb.toString()
    }

    fun parse(serialized: String): List<Item> {
        if (serialized.isEmpty()) return emptyList()
        val out = ArrayList<Item>()
        for (part in serialized.split("\n")) {
            if (part.isBlank()) continue
            parseLine(part)?.let { out.add(it) }
        }
        return out
    }

    fun parseLine(line: String): Item? {
        val fields = line.split(FIELD_SEP)
        if (fields.size >= 2) {
            val t = fields[0]
            val ts = fields[1].toLongOrNull() ?: System.currentTimeMillis()
            val pinned = fields.getOrNull(2) == "1"
            val name = fields.getOrNull(3)?.ifBlank { null }
            return if (t.isNotBlank()) Item(t, ts, pinned, name) else null
        }
        // Legacy format: text:timestamp  (text may contain ':' so split on last ':')
        val idx = line.lastIndexOf(':')
        if (idx > 0) {
            val t = line.substring(0, idx)
            val ts = line.substring(idx + 1).toLongOrNull() ?: System.currentTimeMillis()
            return if (t.isNotBlank()) Item(t, ts) else null
        }
        return null
    }

    /** Add/copy item with deduplication. Returns the updated sorted list. */
    fun push(current: List<Item>, text: String, now: Long): List<Item> {
        val list = current.toMutableList()
        val existing = list.indexOfFirst { it.text == text }
        if (existing >= 0) list.removeAt(existing)
        list.add(0, Item(text, now))
        trimUnpinned(list)
        return list
    }

    /** Apply retention window. Pinned items are always kept. */
    fun prune(current: List<Item>, hours: Long, now: Long): List<Item> {
        val cutoff = now - hours * 3600_000L
        return current.filter { it.pinned || it.timestamp >= cutoff }
    }

    /** Sort pinned first, then by recency. */
    fun sorted(current: List<Item>): List<Item> =
        current.sortedWith(compareByDescending<Item> { it.pinned }.thenByDescending { it.timestamp })

    fun trimUnpinned(list: MutableList<Item>) {
        while (list.count { !it.pinned } > MAX_ITEMS) {
            val idx = list.indexOfLast { !it.pinned }
            if (idx < 0) break
            list.removeAt(idx)
        }
    }

    fun delete(current: List<Item>, text: String): List<Item> =
        current.filterNot { it.text == text }

    fun togglePin(current: List<Item>, text: String): List<Item> =
        current.map { if (it.text == text) it.copy(pinned = !it.pinned) else it }

    fun rename(current: List<Item>, text: String, name: String?): List<Item> =
        current.map { if (it.text == text) it.copy(name = name?.ifBlank { null }) else it }

    fun search(current: List<Item>, query: String): List<Item> {
        val q = query.trim()
        if (q.isEmpty()) return current
        return current.filter {
            it.text.contains(q, ignoreCase = true) || (it.name?.contains(q, ignoreCase = true) == true)
        }
    }
}