package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class XORCipherTest {

    @Test
    fun encryptDecryptRoundTrip() {
        val original = "hello smartkey world 123"
        val encrypted = XORCipher.encrypt(original, "smartkey-test-key")
        assertEquals(original, XORCipher.decrypt(encrypted, "smartkey-test-key"))
    }

    @Test
    fun encryptionChangesContent() {
        val original = "clipboard item that should not be stored in plaintext"
        val encrypted = XORCipher.encrypt(original, "key")
        assertNotEquals(original, encrypted)
    }

    @Test
    fun wrongKeyProducesDifferentText() {
        val original = "sensitive text"
        val encrypted = XORCipher.encrypt(original, "key-one")
        assertNotEquals(original, XORCipher.decrypt(encrypted, "key-two"))
    }

    @Test
    fun decryptingGarbageDoesNotThrow() {
        assertEquals("", XORCipher.decrypt("!!!not-base64!!!", "key"))
    }

    @Test
    fun unicodeSurvivesRoundTrip() {
        val original = "smartkey 😀 ${'$'}{'$'} 200"
        val encrypted = XORCipher.encrypt(original, "uni")
        assertEquals(original, XORCipher.decrypt(encrypted, "uni"))
    }

    @Test
    fun emptyStringRoundTrips() {
        assertEquals("", XORCipher.decrypt(XORCipher.encrypt("", "k"), "k"))
    }

    @Test
    fun largeTextRoundTrips() {
        val original = "word ".repeat(200).trim()
        val encrypted = XORCipher.encrypt(original, "big")
        assertEquals(original, XORCipher.decrypt(encrypted, "big"))
        assertTrue(encrypted.length > 0)
    }
}