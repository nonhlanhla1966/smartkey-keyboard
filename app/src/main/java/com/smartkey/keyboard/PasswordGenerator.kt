package com.smartkey.keyboard

import kotlin.random.Random

/**
 * Offline random password generator. Pure JVM logic, unit testable.
 */
object PasswordGenerator {

    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DIGITS = "0123456789"
    private const val SYMBOLS = "!@#$%^&*()_-+=[]{}<>?,."

    fun generate(
        length: Int = 16,
        useUpper: Boolean = true,
        useDigits: Boolean = true,
        useSymbols: Boolean = true
    ): String {
        val len = length.coerceIn(4, 128)
        var charSet = LOWERCASE
        if (useUpper) charSet += UPPERCASE
        if (useDigits) charSet += DIGITS
        if (useSymbols) charSet += SYMBOLS

        val builder = StringBuilder(len)
        repeat(len) { builder.append(charSet[Random.nextInt(charSet.length)]) }

        // Ensure at least one char from each selected set
        if (useUpper) ensureAtLeast(builder, UPPERCASE)
        if (useDigits) ensureAtLeast(builder, DIGITS)
        if (useSymbols) ensureAtLeast(builder, SYMBOLS)
        return builder.take(len).toString()
    }

    fun generatePin(digits: Int = 6): String {
        val d = digits.coerceIn(4, 16)
        val b = StringBuilder(d)
        repeat(d) { b.append(DIGITS[Random.nextInt(DIGITS.length)]) }
        return b.toString()
    }

    /**
     * Estimate password strength as a simple entropy-like score 0..4.
     */
    fun strength(password: String): Int {
        if (password.isEmpty()) return 0
        var chars = 0
        if (password.any { it in LOWERCASE }) chars += 26
        if (password.any { it in UPPERCASE }) chars += 26
        if (password.any { it in DIGITS }) chars += 10
        if (password.any { it in SYMBOLS }) chars += 32
        val entropy = password.length * Math.log(chars.toDouble()) / Math.log(2.0)
        return when {
            entropy < 28 -> 0
            entropy < 48 -> 1
            entropy < 72 -> 2
            entropy < 96 -> 3
            else -> 4
        }
    }

    private fun ensureAtLeast(builder: StringBuilder, set: String) {
        builder.setCharAt(Random.nextInt(builder.length), set[Random.nextInt(set.length)])
    }
}