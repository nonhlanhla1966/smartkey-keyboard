package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyboardLayoutTest {

    @Test
    fun lettersBaseRowCount() {
        assertEquals(5, KeyboardLayout.rowsFor(KeyboardLayout.MODE_LETTERS, numberRow = false, editingRow = false).size)
    }

    @Test
    fun numberRowAddsTopDigitRow() {
        val rows = KeyboardLayout.rowsFor(KeyboardLayout.MODE_LETTERS, numberRow = true, editingRow = false)
        assertEquals(6, rows.size)
        val top = rows.first()
        assertEquals(10, top.size)
        assertTrue(top.all { it.kind == KeyKind.CHAR && it.text.isNotEmpty() && it.text[0].isDigit() })
    }

    @Test
    fun editingRowAppendedToLetters() {
        val rows = KeyboardLayout.rowsFor(KeyboardLayout.MODE_LETTERS, numberRow = true)
        assertEquals(7, rows.size)
        val last = rows.last()
        val kinds = last.map { it.kind }
        assertTrue(kinds.contains(KeyKind.UNDO))
        assertTrue(kinds.contains(KeyKind.COPY))
        assertTrue(kinds.contains(KeyKind.CURSOR_LEFT))
        assertTrue(kinds.contains(KeyKind.PASTE))
    }

    @Test
    fun symbolModesAlsoGetEditingRow() {
        assertTrue(KeyboardLayout.rowsFor(KeyboardLayout.MODE_SYMBOLS_1).last().any { it.kind == KeyKind.CUT })
        assertTrue(KeyboardLayout.rowsFor(KeyboardLayout.MODE_SYMBOLS_2).last().any { it.kind == KeyKind.REDO })
    }

    @Test
    fun calcLayoutContainsModernOperations() {
        val flat = KeyboardLayout.CALC.flatten()
        val texts = flat.map { it.text }.toSet()
        val kinds = flat.map { it.kind }.toSet()
        assertTrue(texts.contains("√"))
        assertTrue(texts.contains("^"))
        assertTrue(texts.contains("%"))
        assertTrue(texts.contains("±"))
        assertTrue(kinds.contains(KeyKind.CALC_HISTORY))
        assertTrue(kinds.contains(KeyKind.CALC_EQUALS))
    }

    @Test
    fun numberRowHasTenDigits() {
        assertEquals(10, KeyboardLayout.NUMBER_ROW.size)
        assertTrue(KeyboardLayout.NUMBER_ROW.all { it.text == "1" || it.text == "2" || it.text == "3" ||
            it.text == "4" || it.text == "5" || it.text == "6" || it.text == "7" || it.text == "8" ||
            it.text == "9" || it.text == "0" })
    }

    @Test
    fun emojiCategoriesAllHaveIcons() {
        for (i in 0 until EmojiData.CATEGORY_COUNT) {
            assertTrue(EmojiData.categoryIcon(i).isNotBlank())
        }
    }

    @Test
    fun categoryIconResolvesBackToIndex() {
        for (i in 0 until EmojiData.CATEGORY_COUNT) {
            assertEquals(i, EmojiData.categoryIndexOf(EmojiData.categoryIcon(i)))
        }
    }
}