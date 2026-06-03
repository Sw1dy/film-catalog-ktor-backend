package com.example.filmcatalogserver.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordHasherTest {
    @Test
    fun `hash should not be equal to raw password`() {
        val password = "admin123"
        val hash = PasswordHasher.hash(password)

        assertNotEquals(password, hash)
    }

    @Test
    fun `verify should return true for correct password`() {
        val password = "admin123"
        val hash = PasswordHasher.hash(password)

        assertTrue(PasswordHasher.verify(password, hash))
    }

    @Test
    fun `verify should return false for incorrect password`() {
        val password = "admin123"
        val hash = PasswordHasher.hash(password)

        assertFalse(PasswordHasher.verify("wrong-password", hash))
    }
}
