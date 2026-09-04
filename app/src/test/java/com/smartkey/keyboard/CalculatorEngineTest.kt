package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorEngineTest {

    @Test
    fun addition() {
        val e = CalculatorEngine()
        e.append("2")
        e.append("+")
        e.append("3")
        assertEquals("5", e.evaluate())
    }

    @Test
    fun precedenceMultiplyBeforeAdd() {
        val e = CalculatorEngine()
        e.append("7")
        e.append("+")
        e.append("8")
        e.append("*")
        e.append("2")
        assertEquals("23", e.evaluate())
    }

    @Test
    fun multiplication() {
        val e = CalculatorEngine()
        e.append("2")
        e.append("*")
        e.append("3")
        assertEquals("6", e.evaluate())
    }

    @Test
    fun division() {
        val e = CalculatorEngine()
        e.append("9")
        e.append("/")
        e.append("3")
        assertEquals("3", e.evaluate())
    }

    @Test
    fun divisionDecimal() {
        val e = CalculatorEngine()
        e.append("9")
        e.append("/")
        e.append("2")
        assertEquals("4.5", e.evaluate())
    }

    @Test
    fun subtraction() {
        val e = CalculatorEngine()
        e.append("5")
        e.append("-")
        e.append("2")
        assertEquals("3", e.evaluate())
    }

    @Test
    fun parentheses() {
        val e = CalculatorEngine()
        e.append("(")
        e.append("2")
        e.append("+")
        e.append("3")
        e.append(")")
        e.append("*")
        e.append("4")
        assertEquals("20", e.evaluate())
    }

    @Test
    fun percentAfterNumber() {
        val e = CalculatorEngine()
        e.append("1")
        e.append("0")
        e.append("%")
        assertEquals("0.1", e.evaluate())
    }

    @Test
    fun unaryNegative() {
        val e = CalculatorEngine()
        e.append("±")
        e.append("5")
        e.append("+")
        e.append("3")
        assertEquals("-2", e.evaluate())
    }

    @Test
    fun decimals() {
        val e = CalculatorEngine()
        e.append("0.5")
        e.append("+")
        e.append("0.25")
        assertEquals("0.75", e.evaluate())
    }

    @Test
    fun backspace() {
        val e = CalculatorEngine()
        e.append("1")
        e.append("2")
        e.backspace()
        assertEquals("1", e.formatDisplay())
    }

    @Test
    fun clear() {
        val e = CalculatorEngine()
        e.append("1")
        e.append("2")
        e.clear()
        assertTrue(e.expression.isEmpty())
    }

    @Test
    fun invalidExpressionDetected() {
        val e = CalculatorEngine()
        e.append("5")
        e.append("/")
        e.append("0")
        assertFalse(e.isValid())
    }

    @Test
    fun emptyExpressionIsZero() {
        val e = CalculatorEngine()
        assertEquals("0", e.evaluate())
        assertEquals("0", e.formatDisplay())
    }
}