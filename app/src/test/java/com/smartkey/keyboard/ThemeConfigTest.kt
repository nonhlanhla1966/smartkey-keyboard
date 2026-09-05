package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeConfigTest {

    @Test
    fun systemThemeResolvesToDarkWhenDarkFlag() {
        assertEquals(ThemeConfig.KEY_DARK, ThemeConfig.byName(ThemeConfig.KEY_SYSTEM, isDark = true).name)
    }

    @Test
    fun systemThemeResolvesToLightWhenLightFlag() {
        assertEquals(ThemeConfig.KEY_LIGHT, ThemeConfig.byName(ThemeConfig.KEY_SYSTEM, isDark = false).name)
    }

    @Test
    fun namedThemeIgnoresDarkFlag() {
        assertEquals(ThemeConfig.KEY_PINK, ThemeConfig.byName(ThemeConfig.KEY_PINK, isDark = true).name)
    }

    @Test
    fun unknownThemeFallsBackToLight() {
        assertEquals(ThemeConfig.KEY_LIGHT, ThemeConfig.byName("nonexistent", isDark = false).name)
    }

    @Test
    fun allNamedThemesResolve() {
        for (t in ThemeConfig.ALL) {
            assertEquals(t.name, ThemeConfig.byName(t.name, isDark = true).name)
        }
    }
}