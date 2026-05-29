package com.example.filmcatalogserver.data.repository

import com.example.filmcatalogserver.data.dto.RegisterRequest
import com.example.filmcatalogserver.domain.model.AuthUser

data class UserCredentials(
    val user: AuthUser,
    val passwordHash: String
)

interface UserRepository {
    suspend fun findByEmail(email: String): UserCredentials?
    suspend fun create(request: RegisterRequest, passwordHash: String, role: String = "USER"): AuthUser?
}
