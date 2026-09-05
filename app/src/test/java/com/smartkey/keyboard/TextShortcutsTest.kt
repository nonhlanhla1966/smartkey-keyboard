package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TextShortcutsTest {

    private fun shortcuts(): TextShortcuts = TextShortcuts(SmartPrefs(InMemoryPrefs()))

    @Test
    fun emptyByDefault() {
        assertTrue(shortcuts().all().isEmpty())
    }

    @Test
    fun addAndExpand() {
        val s = shortcuts()
        assertTrue(s.put("brb", "be right back"))
        assertEquals("be right back", s.expand("brb"))
        assertEquals("be right back", s.expand("BRB"))
    }

    @Test
    fun rejectEmptyOrTooLong() {
        val s = shortcuts()
        assertFalse(s.put("", "x"))
        assertFalse(s.put("x", " "))
        assertFalse(s.put("abcdefghijklmnopqrstuvwxyz1", "x"))
        assertTrue(s.put("ok", "okay"))
    }

    @Test
    fun removeDeletesEntry() {
        val s = shortcuts()
        s.put("idk", "i don't know")
        s.remove("IDK")
        assertNull(s.expand("idk"))
    }

    @Test
    fun persistsAcrossInstances() {
        val prefs = SmartPrefs(InMemoryPrefs())
        TextShortcuts(prefs).put("omw", "on my way")
        val reloaded = TextShortcuts(prefs)
        assertEquals("on my way", reloaded.expand("omw"))
        assertEquals(1, reloaded.all().size)
    }

    @Test
    fun unknownWordNoExpansion() {
        assertNull(shortcuts().expand("zzzz"))
    }
}