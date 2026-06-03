package com.example.filmcatalogserver.validation

import com.example.filmcatalogserver.data.dto.LoginRequest
import com.example.filmcatalogserver.data.dto.RegisterRequest
import com.example.filmcatalogserver.util.validateLoginRequest
import com.example.filmcatalogserver.util.validateRegisterRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthValidationTest {
    @Test
    fun `successful registration should pass`() {
        val request = validRegisterRequest()

        assertNull(validateRegisterRequest(request))
    }

    @Test
    fun `blank first name should return error`() {
        val request = validRegisterRequest(firstName = "   ")

        assertEquals("Имя не должно быть пустым", validateRegisterRequest(request))
    }

    @Test
    fun `blank last name should return error`() {
        val request = validRegisterRequest(lastName = "   ")

        assertEquals("Фамилия не должна быть пустой", validateRegisterRequest(request))
    }

    @Test
    fun `invalid registration email should return error`() {
        val request = validRegisterRequest(email = "invalid-email")

        assertEquals("Некорректный email", validateRegisterRequest(request))
    }

    @Test
    fun `short password should return error`() {
        val request = validRegisterRequest(password = "12345")

        assertEquals("Пароль должен быть не короче 6 символов", validateRegisterRequest(request))
    }

    @Test
    fun `too long email should return error`() {
        val longEmail = "${"a".repeat(101)}@test.ru"
        val request = validRegisterRequest(email = longEmail)

        assertEquals("Email не должен быть длиннее 100 символов", validateRegisterRequest(request))
    }

    @Test
    fun `successful login should pass`() {
        val request = LoginRequest(email = "admin@test.ru", password = "admin123")

        assertNull(validateLoginRequest(request))
    }

    @Test
    fun `blank login email should return error`() {
        val request = LoginRequest(email = "   ", password = "admin123")

        assertEquals("Email не должен быть пустым", validateLoginRequest(request))
    }

    @Test
    fun `invalid login email should return error`() {
        val request = LoginRequest(email = "invalid-email", password = "admin123")

        assertEquals("Некорректный email", validateLoginRequest(request))
    }

    @Test
    fun `blank login password should return error`() {
        val request = LoginRequest(email = "admin@test.ru", password = "")

        assertEquals("Пароль не должен быть пустым", validateLoginRequest(request))
    }

    private fun validRegisterRequest(
        firstName: String = "Никита",
        lastName: String = "Породин",
        email: String = "nikita@test.ru",
        password: String = "123456"
    ): RegisterRequest =
        RegisterRequest(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password
        )
}
