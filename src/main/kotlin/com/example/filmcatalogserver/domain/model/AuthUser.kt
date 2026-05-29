package com.example.filmcatalogserver.domain.model

data class AuthUser(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String
)
