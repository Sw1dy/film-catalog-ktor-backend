package com.example.filmcatalogserver.util

import com.example.filmcatalogserver.data.dto.LoginRequest
import com.example.filmcatalogserver.data.dto.RegisterRequest

private const val MAX_NAME_LENGTH = 50
private const val MAX_EMAIL_LENGTH = 100
private const val MIN_PASSWORD_LENGTH = 6
private const val MAX_PASSWORD_LENGTH = 100

private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

fun validateRegisterRequest(request: RegisterRequest): String? {
    val firstName = request.firstName.trim()
    val lastName = request.lastName.trim()
    val email = request.email.trim()

    return when {
        firstName.isEmpty() -> "Имя не должно быть пустым"
        lastName.isEmpty() -> "Фамилия не должна быть пустой"
        email.isEmpty() -> "Email не должен быть пустым"
        email.length > MAX_EMAIL_LENGTH -> "Email не должен быть длиннее 100 символов"
        !emailRegex.matches(email) -> "Некорректный email"
        request.password.isEmpty() -> "Пароль не должен быть пустым"
        request.password.length < MIN_PASSWORD_LENGTH -> "Пароль должен быть не короче 6 символов"
        request.password.length > MAX_PASSWORD_LENGTH -> "Пароль не должен быть длиннее 100 символов"
        firstName.length > MAX_NAME_LENGTH -> "Имя не должно быть длиннее 50 символов"
        lastName.length > MAX_NAME_LENGTH -> "Фамилия не должна быть длиннее 50 символов"
        else -> null
    }
}

fun validateLoginRequest(request: LoginRequest): String? {
    val email = request.email.trim()

    return when {
        email.isEmpty() -> "Email не должен быть пустым"
        !emailRegex.matches(email) -> "Некорректный email"
        request.password.isEmpty() -> "Пароль не должен быть пустым"
        else -> null
    }
}
