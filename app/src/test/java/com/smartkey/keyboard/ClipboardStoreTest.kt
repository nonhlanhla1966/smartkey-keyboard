package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipboardStoreTest {

    private val now = 1_700_000_000_000L

    @Test
    fun serializeParseRoundTrip() {
        val items = listOf(
            ClipboardStore.Item("hello", now, pinned = true, name = "Greeting"),
            ClipboardStore.Item("world", now + 1000)
        )
        val parsed = ClipboardStore.parse(ClipboardStore.serialize(items))
        assertEquals(2, parsed.size)
        assertEquals("hello", parsed[0].text)
        assertEquals(true, parsed[0].pinned)
        assertEquals("Greeting", parsed[0].name)
    }

    @Test
    fun legacyFormatParses() {
        val parsed = ClipboardStore.parse("hello-world:1700000000000\n")
        assertEquals(1, parsed.size)
        assertEquals("hello-world", parsed[0].text)
        assertEquals(1700000000000L, parsed[0].timestamp)
    }

    @Test
    fun pushDeduplicatesAndSortsNewestFirst() {
        var list = ClipboardStore.push(emptyList(), "first", now)
        list = ClipboardStore.push(list, "second", now + 100)
        list = ClipboardStore.push(list, "first", now + 200)
        assertEquals(2, list.size)
        // "first" moved to front, "second" dropped to position 1
        assertEquals("first", ClipboardStore.sorted(list)[0].text)
        assertEquals("second", ClipboardStore.sorted(list)[1].text)
    }

    @Test
    fun pruneKeepsRecentAndPinned() {
        val items = listOf(
            ClipboardStore.Item("old", now - 10000, pinned = true),
            ClipboardStore.Item("recent", now - 1000),
            ClipboardStore.Item("ancient", now - 100 * 3600_000L)
        )
        val pruned = ClipboardStore.prune(items, 12, now)
        assertEquals(2, pruned.size)
        assertTrue(pruned.any { it.text == "old" })
        assertTrue(pruned.any { it.text == "recent" })
        assertFalse(pruned.any { it.text == "ancient" })
    }

    @Test
    fun trimUnpinnedCapsAtMax() {
        val list = (0 until 50).map { ClipboardStore.Item("item-$it", now - it) }.toMutableList()
        ClipboardStore.trimUnpinned(list)
        assertTrue(list.size <= ClipboardStore.MAX_ITEMS)
    }

    @Test
    fun togglePin() {
        val items = listOf(ClipboardStore.Item("a", now))
        val toggled = ClipboardStore.togglePin(items, "a")
        assertTrue(toggled[0].pinned)
        val untoggled = ClipboardStore.togglePin(toggled, "a")
        assertFalse(untoggled[0].pinned)
    }

    @Test
    fun rename() {
        val items = listOf(ClipboardStore.Item("a", now))
        val renamed = ClipboardStore.rename(items, "a", "My note")
        assertEquals("My note", renamed[0].name)
    }

    @Test
    fun searchMatchesTextAndName() {
        val items = listOf(
            ClipboardStore.Item("the quick brown fox", now, name = "foxy"),
            ClipboardStore.Item("nothing here", now)
        )
        assertEquals(1, ClipboardStore.search(items, "quick").size)
        assertEquals(1, ClipboardStore.search(items, "FOXY").size)
    }

    @Test
    fun deleteRemovesByText() {
        val items = listOf(ClipboardStore.Item("a", now), ClipboardStore.Item("b", now + 1))
        val after = ClipboardStore.delete(items, "a")
        assertEquals(1, after.size)
        assertEquals("b", after[0].text)
    }
}