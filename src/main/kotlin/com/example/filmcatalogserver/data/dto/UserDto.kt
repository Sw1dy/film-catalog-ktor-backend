package com.example.filmcatalogserver.data.dto

import com.example.filmcatalogserver.domain.model.AuthUser
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String
)

fun AuthUser.toDto(): UserDto =
    UserDto(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        role = role
    )
