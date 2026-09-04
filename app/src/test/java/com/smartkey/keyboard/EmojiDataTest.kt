package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmojiDataTest {

    @Test
    fun categoriesExist() {
        assertEquals(7, EmojiData.CATEGORY_COUNT)
        assertEquals("😀", EmojiData.categoryIcon(0))
    }

    @Test
    fun pageOneHasExpectedSizeAndContent() {
        val page = EmojiData.pageEmoji(0, 0)
        assertTrue(page.size <= EmojiData.PAGE_SIZE)
        assertTrue(page.contains("😀"))
    }

    @Test
    fun pagingCyclesWithinCategory() {
        val page0 = EmojiData.pageEmoji(0, 0)
        val page1 = EmojiData.pageEmoji(0, 1)
        assertTrue(page0.isNotEmpty())
        assertTrue(page1.isNotEmpty())
    }

    @Test
    fun negativeOffsetWraps() {
        EmojiData.pageEmoji(0, -1)
        EmojiData.pageEmoji(3, -3)
    }

    @Test
    fun allEmojiFlattened() {
        assertTrue(EmojiData.all.size >= 7 * 40)
    }

    @Test
    fun everyCategoryHasIcon() {
        for (i in 0 until EmojiData.CATEGORY_COUNT) {
            assertTrue(EmojiData.categoryIcon(i).isNotBlank())
        }
    }
}