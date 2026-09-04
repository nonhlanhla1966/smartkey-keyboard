package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SuggestionsEngineTest {

    private lateinit var prefs: SmartPrefs
    private lateinit var engine: SuggestionsEngine

    @Before
    fun setup() {
        prefs = SmartPrefs(InMemoryPrefs())
        engine = SuggestionsEngine(prefs)
        engine.clear()
    }

    @Test
    fun suggestsCommonWordByPrefix() {
        val s = engine.suggest("th")
        assertEquals(true, s.contains("the"))
    }

    @Test
    fun emptyPrefixYieldsNothing() {
        assertEquals(true, engine.suggest("").isEmpty())
    }

    @Test
    fun learnsTypedWord() {
        engine.learn("smartkey")
        val s = engine.suggest("sm")
        assertEquals(true, s.contains("smartkey"))
    }

    @Test
    fun shouldCapitalizeAtStart() {
        assertEquals(true, engine.shouldCapitalize(""))
    }

    @Test
    fun shouldCapitalizeAfterPeriod() {
        assertEquals(true, engine.shouldCapitalize("Hello world. "))
    }

    @Test
    fun shouldNotCapitalizeMidSentence() {
        assertEquals(false, engine.shouldCapitalize("this is "))
    }

    @Test
    fun autocorrectKnownMisspelling() {
        val fix = engine.correct("teh")
        assertEquals("the", fix)
    }

    @Test
    fun autocorrectNoChangeForCorrectWord() {
        assertNull(engine.correct("the"))
    }

    @Test
    fun autocorrectTooShortReturnsNull() {
        assertNull(engine.correct("ab"))
    }

    @Test
    fun learningDisabledStopsLearnedSuggestions() {
        prefs.putBoolean(SmartPrefs.KEY_LEARNING, false)
        engine.learn("smartkey")
        val s = engine.suggest("sm")
        assertEquals(false, s.contains("smartkey"))
    }

    @Test
    fun clearRemovesLearnedWords() {
        engine.learn("smartkey")
        engine.clear()
        assertEquals(false, engine.suggest("sm").contains("smartkey"))
    }
}