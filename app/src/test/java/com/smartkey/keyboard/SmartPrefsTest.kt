package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartPrefsTest {

    private fun prefs(): SmartPrefs = SmartPrefs(InMemoryPrefs())

    @Test
    fun stringsPersist() {
        val p = prefs()
        p.putString(SmartPrefs.KEY_THEME, "dark")
        assertEquals("dark", p.getString(SmartPrefs.KEY_THEME))
    }

    @Test
    fun booleansPersist() {
        val p = prefs()
        p.putBoolean(SmartPrefs.KEY_SUGGESTIONS, false)
        assertFalse(p.getBoolean(SmartPrefs.KEY_SUGGESTIONS, true))
        assertTrue(p.getBoolean(SmartPrefs.KEY_NUMBER_ROW, false))
    }

    @Test
    fun intsPersist() {
        val p = prefs()
        p.putInt(SmartPrefs.KEY_CLIPBOARD_HOURS, 24)
        assertEquals(24, p.getInt(SmartPrefs.KEY_CLIPBOARD_HOURS, 12))
    }

    @Test
    fun defaultsAppliedWhenUnset() {
        val p = prefs()
        assertEquals("light", p.getString(SmartPrefs.KEY_THEME) ?: "light")
        assertEquals(true, p.getBoolean(SmartPrefs.KEY_HAPTICS, true))
        assertEquals(12, p.getInt(SmartPrefs.KEY_CLIPBOARD_HOURS, 12))
        assertNull(p.getString(SmartPrefs.KEY_CLIPBOARD_PIN))
    }

    @Test
    fun removeClearsValue() {
        val p = prefs()
        p.putString(SmartPrefs.KEY_THEME, "green")
        p.remove(SmartPrefs.KEY_THEME)
        assertNull(p.getString(SmartPrefs.KEY_THEME))
    }

    @Test
    fun keyHeightPreferenceMapsToSize() {
        val p = prefs()
        p.putString(SmartPrefs.KEY_KEY_HEIGHT, "large")
        assertEquals("large", p.getString(SmartPrefs.KEY_KEY_HEIGHT))
    }

    @Test
    fun autocorrectDefaultsOn() {
        val p = prefs()
        assertEquals(true, p.getBoolean(SmartPrefs.KEY_AUTOCORRECT, true))
    }

    @Test
    fun oneHandedDefaultsOff() {
        val p = prefs()
        assertEquals(0, p.getInt(SmartPrefs.KEY_ONE_HANDED, 0))
    }
}