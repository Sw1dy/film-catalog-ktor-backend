package com.example.filmcatalogserver.routing

import com.example.filmcatalogserver.data.dto.CreateMovieRequest
import com.example.filmcatalogserver.data.dto.ErrorResponse
import com.example.filmcatalogserver.data.dto.UpdateMovieRequest
import com.example.filmcatalogserver.data.dto.toDto
import com.example.filmcatalogserver.data.repository.MovieRepository
import com.example.filmcatalogserver.util.requireAdmin
import com.example.filmcatalogserver.util.validateCreateMovieRequest
import com.example.filmcatalogserver.util.validateUpdateMovieRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.movieRoutes(repository: MovieRepository) {
    route("/movies") {
        get {
            val movies = repository.getAll().map { it.toDto() }
            call.respond(HttpStatusCode.OK, movies)
        }

        get("/search") {
            val query = call.request.queryParameters["query"]?.trim()
            if (query.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Параметр query не должен быть пустым"))
                return@get
            }

            val movies = repository.searchByTitle(query).map { it.toDto() }
            call.respond(HttpStatusCode.OK, movies)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Некорректный id фильма"))
                return@get
            }

            val movie = repository.getById(id)
            if (movie == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Фильм не найден"))
            } else {
                call.respond(HttpStatusCode.OK, movie.toDto())
            }
        }

        post {
            if (!call.requireAdmin()) {
                return@post
            }

            val request = call.receive<CreateMovieRequest>()
            val validationError = validateCreateMovieRequest(request)
            if (validationError != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(validationError))
                return@post
            }

            val movie = repository.create(request)
            call.respond(HttpStatusCode.Created, movie.toDto())
        }

        put("/{id}") {
            if (!call.requireAdmin()) {
                return@put
            }

            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Некорректный id фильма"))
                return@put
            }

            val request = call.receive<UpdateMovieRequest>()
            val validationError = validateUpdateMovieRequest(request)
            if (validationError != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(validationError))
                return@put
            }

            val movie = repository.update(id, request)
            if (movie == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Фильм не найден"))
            } else {
                call.respond(HttpStatusCode.OK, movie.toDto())
            }
        }

        delete("/{id}") {
            if (!call.requireAdmin()) {
                return@delete
            }

            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Некорректный id фильма"))
                return@delete
            }

            val deleted = repository.delete(id)
            if (!deleted) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Фильм не найден"))
            } else {
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
