package com.example.filmcatalogserver.util

import com.example.filmcatalogserver.domain.model.AuthUser
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object SessionStorage {
    private val sessions = ConcurrentHashMap<String, AuthUser>()

    fun createSession(user: AuthUser): String {
        val token = UUID.randomUUID().toString()
        sessions[token] = user
        return token
    }

    fun getUser(token: String): AuthUser? = sessions[token]
}
