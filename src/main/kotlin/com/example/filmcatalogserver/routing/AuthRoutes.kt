package com.example.filmcatalogserver.routing

import com.example.filmcatalogserver.data.dto.AuthResponse
import com.example.filmcatalogserver.data.dto.ErrorResponse
import com.example.filmcatalogserver.data.dto.LoginRequest
import com.example.filmcatalogserver.data.dto.RegisterRequest
import com.example.filmcatalogserver.data.dto.toDto
import com.example.filmcatalogserver.data.repository.UserRepository
import com.example.filmcatalogserver.util.JwtService
import com.example.filmcatalogserver.util.PasswordHasher
import com.example.filmcatalogserver.util.getCurrentUser
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(repository: UserRepository) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val validationError = validateRegisterRequest(request)
            if (validationError != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(validationError))
                return@post
            }

            val passwordHash = PasswordHasher.hash(request.password)
            val user = repository.create(request, passwordHash)
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Пользователь с таким email уже существует"))
                return@post
            }

            val token = JwtService.generateToken(user)
            call.respond(HttpStatusCode.Created, AuthResponse(token, user.toDto()))
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            if (request.email.isBlank() || request.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Email и пароль не должны быть пустыми"))
                return@post
            }

            val credentials = repository.findByEmail(request.email)
            if (credentials == null || !PasswordHasher.verify(request.password, credentials.passwordHash)) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Неверный email или пароль"))
                return@post
            }

            val token = JwtService.generateToken(credentials.user)
            call.respond(HttpStatusCode.OK, AuthResponse(token, credentials.user.toDto()))
        }

        get("/me") {
            val user = getCurrentUser(call)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Необходима авторизация"))
                return@get
            }

            call.respond(HttpStatusCode.OK, user.toDto())
        }
    }
}

private fun validateRegisterRequest(request: RegisterRequest): String? {
    return when {
        request.firstName.isBlank() -> "Имя не должно быть пустым"
        request.lastName.isBlank() -> "Фамилия не должна быть пустой"
        request.email.isBlank() -> "Email не должен быть пустым"
        request.password.isBlank() -> "Пароль не должен быть пустым"
        else -> null
    }
}
