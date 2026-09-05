package com.smartkey.keyboard

import org.junit.Assert.assertTrue
import org.junit.Test

class EmojiSearchTest {

    @Test
    fun blankQueryYieldsNothing() {
        assertTrue(EmojiData.search("").isEmpty())
        assertTrue(EmojiData.search("   ").isEmpty())
    }

    @Test
    fun findsHeart() {
        assertTrue(EmojiData.search("heart").contains("❤️"))
        assertTrue(EmojiData.search("broken heart").contains("💔"))
    }

    @Test
    fun findsDogAndCat() {
        val dogs = EmojiData.search("dog")
        assertTrue(dogs.contains("🐶"))
        assertTrue(EmojiData.search("cat").contains("🐱"))
    }

    @Test
    fun multiWordSearch() {
        assertTrue(EmojiData.search("polar bear").contains("🐻‍❄️"))
    }

    @Test
    fun searchIsCaseInsensitive() {
        assertTrue(EmojiData.search("HEART").contains("❤️"))
        assertTrue(EmojiData.search("Heart").contains("❤️"))
    }

    @Test
    fun respectsLimit() {
        assertTrue(EmojiData.search("star", limit = 5).size <= 5)
    }

    @Test
    fun kitchenSearch() {
        val kitchen = EmojiData.search("pizza")
        assertTrue(kitchen.isNotEmpty())
        assertTrue(EmojiData.search("sushi").contains("🍣"))
    }

    @Test
    fun unknownQueryEmpty() {
        assertTrue(EmojiData.search("qqqqxxyy").isEmpty())
    }
}