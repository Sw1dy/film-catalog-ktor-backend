package com.example.filmcatalogserver.util

import com.example.filmcatalogserver.data.dto.ErrorResponse
import com.example.filmcatalogserver.domain.model.AuthUser
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header
import io.ktor.server.response.respond

private const val BEARER_PREFIX = "Bearer "
private const val ROLE_ADMIN = "ADMIN"

fun getCurrentUser(call: ApplicationCall): AuthUser? {
    val token = call.request.header(HttpHeaders.Authorization)
        ?.takeIf { it.startsWith(BEARER_PREFIX) }
        ?.removePrefix(BEARER_PREFIX)
        ?.trim()

    return token?.takeIf { it.isNotBlank() }?.let(SessionStorage::getUser)
}

suspend fun ApplicationCall.requireAdmin(): Boolean {
    val user = getCurrentUser(this)
    if (user == null) {
        respond(HttpStatusCode.Unauthorized, ErrorResponse("Необходима авторизация"))
        return false
    }

    if (user.role != ROLE_ADMIN) {
        respond(HttpStatusCode.Forbidden, ErrorResponse("Доступ запрещён"))
        return false
    }

    return true
}
