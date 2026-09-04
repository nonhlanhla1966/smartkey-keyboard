package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordGeneratorTest {

    @Test
    fun generatesRequestedLength() {
        val p = PasswordGenerator.generate(20)
        assertEquals(20, p.length)
    }

    @Test
    fun includesAllSelectedSets() {
        val p = PasswordGenerator.generate(20, useUpper = true, useDigits = true, useSymbols = true)
        assertTrue(p.any { it in "abcdefghijklmnopqrstuvwxyz" })
        assertTrue(p.any { it in "ABCDEFGHIJKLMNOPQRSTUVWXYZ" })
        assertTrue(p.any { it.isDigit() })
        assertTrue(p.any { it in "!@#\$%^&*()_-+=[]{}<>?,." })
    }

    @Test
    fun noSymbolsWhenDisabled() {
        val p = PasswordGenerator.generate(30, useSymbols = false)
        assertTrue(p.none { it in "!@#\$%^&*()_-+=[]{}<>?,." })
    }

    @Test
    fun differentOutputs() {
        val a = PasswordGenerator.generate(20, useUpper = true, useDigits = true, useSymbols = true)
        val b = PasswordGenerator.generate(20, useUpper = true, useDigits = true, useSymbols = true)
        val set = mutableSetOf(a, b)
        assertEquals(true, set.size > 1)
    }

    @Test
    fun pinHasOnlyDigits() {
        val pin = PasswordGenerator.generatePin(6)
        assertEquals(6, pin.length)
        assertTrue(pin.all { it.isDigit() })
    }

    @Test
    fun strengthOfLongStrongPassword() {
        val p = "Ab3\$x9!qW2@zZ7"
        assertEquals(4, PasswordGenerator.strength(p))
    }

    @Test
    fun strengthOfWeakPassword() {
        assertEquals(0, PasswordGenerator.strength("12"))
    }
}