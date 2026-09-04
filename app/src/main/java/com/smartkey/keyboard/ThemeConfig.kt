package com.smartkey.keyboard

import android.graphics.Color

data class Theme(
    val name: String,
    val bg: Int,
    val key: Int,
    val keyDark: Int,
    val keyText: Int,
    val accent: Int,
    val accentText: Int,
    val panel: Int,
    val panelDark: Int,
    val dimText: Int
)

object ThemeConfig {
    const val KEY_LIGHT = "light"
    const val KEY_DARK = "dark"
    const val KEY_BLUE = "blue"
    const val KEY_GREEN = "green"
    const val KEY_PURPLE = "purple"
    const val KEY_ORANGE = "orange"
    const val KEY_TEAL = "teal"
    const val KEY_PINK = "pink"

    fun light(): Theme = Theme(
        KEY_LIGHT,
        bg = Color.rgb(0xE5, 0xE6, 0xEB),
        key = Color.WHITE,
        keyDark = Color.rgb(0xCE, 0xD1, 0xD8),
        keyText = Color.rgb(0x1C, 0x1C, 0x1E),
        accent = Color.rgb(0x0A, 0x84, 0xFF),
        accentText = Color.WHITE,
        panel = Color.WHITE,
        panelDark = Color.rgb(0xE5, 0xE7, 0xEC),
        dimText = Color.rgb(0x6B, 0x6E, 0x74)
    )

    fun dark(): Theme = Theme(
        KEY_DARK,
        bg = Color.rgb(0x1C, 0x1C, 0x1E),
        key = Color.rgb(0x2C, 0x2C, 0x2E),
        keyDark = Color.rgb(0x3A, 0x3A, 0x3C),
        keyText = Color.rgb(0xF2, 0xF2, 0xF7),
        accent = Color.rgb(0x0A, 0x84, 0xFF),
        accentText = Color.WHITE,
        panel = Color.rgb(0x28, 0x28, 0x2A),
        panelDark = Color.rgb(0x38, 0x38, 0x3A),
        dimText = Color.rgb(0x9A, 0x9A, 0xA0)
    )

    fun blue(): Theme = Theme(
        KEY_BLUE,
        bg = Color.rgb(0x11, 0x2D, 0x4A),
        key = Color.rgb(0x1E, 0x45, 0x6B),
        keyDark = Color.rgb(0x2A, 0x55, 0x7F),
        keyText = Color.WHITE,
        accent = Color.rgb(0x40, 0xB4, 0xFF),
        accentText = Color.rgb(0x00, 0x22, 0x3B),
        panel = Color.rgb(0x1F, 0x47, 0x6E),
        panelDark = Color.rgb(0x2F, 0x57, 0x7E),
        dimText = Color.rgb(0xA9, 0xC8, 0xE0)
    )

    fun green(): Theme = Theme(
        KEY_GREEN,
        bg = Color.rgb(0x11, 0x2A, 0x22),
        key = Color.rgb(0x1F, 0x41, 0x33),
        keyDark = Color.rgb(0x29, 0x51, 0x41),
        keyText = Color.rgb(0xED, 0xFF, 0xF5),
        accent = Color.rgb(0x30, 0xD1, 0x58),
        accentText = Color.rgb(0x00, 0x33, 0x12),
        panel = Color.rgb(0x20, 0x43, 0x35),
        panelDark = Color.rgb(0x30, 0x53, 0x45),
        dimText = Color.rgb(0xAE, 0xD4, 0xBF)
    )

    fun purple(): Theme = Theme(
        KEY_PURPLE,
        bg = Color.rgb(0x28, 0x1E, 0x3D),
        key = Color.rgb(0x3F, 0x31, 0x5F),
        keyDark = Color.rgb(0x4D, 0x3D, 0x71),
        keyText = Color.rgb(0xF5, 0xEF, 0xFF),
        accent = Color.rgb(0xBF, 0x5A, 0xF2),
        accentText = Color.rgb(0x2B, 0x00, 0x53),
        panel = Color.rgb(0x41, 0x34, 0x62),
        panelDark = Color.rgb(0x53, 0x44, 0x74),
        dimText = Color.rgb(0xC6, 0xB8, 0xDE)
    )

    fun orange(): Theme = Theme(
        KEY_ORANGE,
        bg = Color.rgb(0x3A, 0x22, 0x0D),
        key = Color.rgb(0x57, 0x35, 0x19),
        keyDark = Color.rgb(0x6B, 0x42, 0x1E),
        keyText = Color.rgb(0xFF, 0xF3, 0xE7),
        accent = Color.rgb(0xFF, 0x9F, 0x0A),
        accentText = Color.rgb(0x40, 0x22, 0x00),
        panel = Color.rgb(0x5A, 0x3A, 0x1E),
        panelDark = Color.rgb(0x6E, 0x4C, 0x2C),
        dimText = Color.rgb(0xE8, 0xC9, 0xA8)
    )

    fun teal(): Theme = Theme(
        KEY_TEAL,
        bg = Color.rgb(0x0D, 0x2E, 0x2E),
        key = Color.rgb(0x1A, 0x47, 0x47),
        keyDark = Color.rgb(0x23, 0x57, 0x57),
        keyText = Color.rgb(0xE8, 0xFF, 0xFE),
        accent = Color.rgb(0x64, 0xD2, 0xCB),
        accentText = Color.rgb(0x00, 0x33, 0x30),
        panel = Color.rgb(0x1C, 0x49, 0x49),
        panelDark = Color.rgb(0x2C, 0x59, 0x59),
        dimText = Color.rgb(0xAC, 0xD6, 0xD4)
    )

    fun pink(): Theme = Theme(
        KEY_PINK,
        bg = Color.rgb(0x3A, 0x14, 0x26),
        key = Color.rgb(0x57, 0x21, 0x38),
        keyDark = Color.rgb(0x6B, 0x2A, 0x45),
        keyText = Color.rgb(0xFF, 0xF0, 0xF5),
        accent = Color.rgb(0xFF, 0x2D, 0x92),
        accentText = Color.rgb(0x3F, 0x00, 0x1E),
        panel = Color.rgb(0x5A, 0x24, 0x3C),
        panelDark = Color.rgb(0x6E, 0x30, 0x4C),
        dimText = Color.rgb(0xE8, 0xBC, 0xCE)
    )

    val ALL = listOf(light(), dark(), blue(), green(), purple(), orange(), teal(), pink())

    fun byName(name: String): Theme = ALL.firstOrNull { it.name.equals(name, true) } ?: light()
}