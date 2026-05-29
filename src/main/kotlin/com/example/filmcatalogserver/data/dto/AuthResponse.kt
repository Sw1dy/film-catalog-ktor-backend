package com.example.filmcatalogserver.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto
)
