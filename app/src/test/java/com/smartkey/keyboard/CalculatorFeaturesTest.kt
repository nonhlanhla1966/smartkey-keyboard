package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorFeaturesTest {

    @Test
    fun powers() {
        val e = CalculatorEngine()
        e.append("2")
        e.append("^")
        e.append("3")
        assertEquals("8", e.evaluate())
    }

    @Test
    fun powerPrecedenceOverMultiply() {
        val e = CalculatorEngine()
        e.append("2")
        e.append("*")
        e.append("3")
        e.append("^")
        e.append("2")
        assertEquals("18", e.evaluate())
    }

    @Test
    fun squareRoot() {
        val e = CalculatorEngine()
        e.append("√")
        e.append("9")
        assertEquals("3", e.evaluate())
    }

    @Test
    fun squareRootOfNegativeIsError() {
        val e = CalculatorEngine()
        e.append("√")
        e.append("-9")
        assertEquals("Error", e.evaluate())
    }

    @Test
    fun percentWithMultiply() {
        val e = CalculatorEngine()
        e.append("2")
        e.append("0")
        e.append("0")
        e.append("*")
        e.append("1")
        e.append("0")
        e.append("%")
        assertEquals("20", e.evaluate())
    }

    @Test
    fun percentExampleTwoFiftyTimesFifteen() {
        val e = CalculatorEngine()
        e.append("2")
        e.append("5")
        e.append("0")
        e.append("*")
        e.append("1")
        e.append("5")
        e.append("%")
        assertEquals("37.5", e.evaluate())
    }

    @Test
    fun parenthesesExample() {
        val e = CalculatorEngine()
        e.append("(")
        e.append("2")
        e.append("5")
        e.append("+")
        e.append("1")
        e.append("5")
        e.append(")")
        e.append("*")
        e.append("3")
        assertEquals("120", e.evaluate())
    }

    @Test
    fun divisionExample() {
        val e = CalculatorEngine()
        e.append("1")
        e.append("2")
        e.append("5")
        e.append("0")
        e.append("/")
        e.append("5")
        assertEquals("250", e.evaluate())
    }

    @Test
    fun divisionByZeroNotValid() {
        val e = CalculatorEngine()
        e.append("5")
        e.append("/")
        e.append("0")
        assertTrue(!e.isValid())
    }

    @Test
    fun historyRecordsEvaluations() {
        val e = CalculatorEngine()
        e.append("2")
        e.append("+")
        e.append("2")
        e.evaluate()
        e.append("3")
        e.append("*")
        e.append("3")
        e.evaluate()
        assertEquals(2, e.history.size)
        val latest = e.history.last()
        assertEquals("3*3", latest.expr)
        assertEquals("9", latest.result)
    }

    @Test
    fun historyRecallSetsExpression() {
        val e = CalculatorEngine()
        e.append("1")
        e.append("0")
        e.append("/")
        e.append("2")
        e.evaluate()
        e.clear()
        val entry = e.recallHistory(0)
        assertEquals("5", entry?.result)
        assertEquals("5", e.formatDisplay())
    }

    @Test
    fun clearHistoryWorks() {
        val e = CalculatorEngine()
        e.append("1")
        e.append("+")
        e.append("1")
        e.evaluate()
        e.clearHistory()
        assertTrue(e.history.isEmpty())
    }
}