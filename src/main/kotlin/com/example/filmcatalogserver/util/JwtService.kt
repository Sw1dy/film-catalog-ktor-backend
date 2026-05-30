package com.example.filmcatalogserver.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.example.filmcatalogserver.domain.model.AuthUser
import java.util.Date

object JwtService {
    private const val SECRET = "film-catalog-secret-key-change-later"
    private const val ISSUER = "film-catalog-server"
    private const val AUDIENCE = "film-catalog-android"
    private const val EXPIRATION_TIME_MILLIS = 1000L * 60L * 60L * 24L * 30L

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateToken(user: AuthUser): String {
        val now = System.currentTimeMillis()

        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + EXPIRATION_TIME_MILLIS))
            .withClaim("id", user.id)
            .withClaim("firstName", user.firstName)
            .withClaim("lastName", user.lastName)
            .withClaim("email", user.email)
            .withClaim("role", user.role)
            .sign(algorithm)
    }

    fun verifyToken(token: String): AuthUser? {
        return try {
            val verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .build()

            val decodedJwt = verifier.verify(token)

            AuthUser(
                id = decodedJwt.getClaim("id").asInt(),
                firstName = decodedJwt.getClaim("firstName").asString(),
                lastName = decodedJwt.getClaim("lastName").asString(),
                email = decodedJwt.getClaim("email").asString(),
                role = decodedJwt.getClaim("role").asString()
            )
        } catch (_: JWTVerificationException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        } catch (_: NullPointerException) {
            null
        }
    }
}
