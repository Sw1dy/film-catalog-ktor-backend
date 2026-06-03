package com.example.filmcatalogserver.util

import com.example.filmcatalogserver.domain.model.AuthUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JwtServiceTest {
    private val user = AuthUser(
        id = 1,
        firstName = "Админ",
        lastName = "Каталога",
        email = "admin@test.ru",
        role = "ADMIN"
    )

    @Test
    fun `generateToken should create non blank token`() {
        val token = JwtService.generateToken(user)

        assertTrue(token.isNotBlank())
    }

    @Test
    fun `generated token should contain three parts`() {
        val token = JwtService.generateToken(user)

        assertEquals(3, token.split(".").size)
    }

    @Test
    fun `verifyToken should return auth user for valid token`() {
        val token = JwtService.generateToken(user)

        val decodedUser = JwtService.verifyToken(token)

        assertNotNull(decodedUser)
        assertEquals(user.id, decodedUser.id)
        assertEquals(user.firstName, decodedUser.firstName)
        assertEquals(user.lastName, decodedUser.lastName)
        assertEquals(user.email, decodedUser.email)
        assertEquals(user.role, decodedUser.role)
    }

    @Test
    fun `verifyToken should return null for invalid token`() {
        assertNull(JwtService.verifyToken("invalid.token.value"))
    }
}
