package com.example.filmcatalogserver.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)
