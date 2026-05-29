package com.example.filmcatalogserver.util

import com.example.filmcatalogserver.data.dto.CreateMovieRequest
import com.example.filmcatalogserver.data.dto.UpdateMovieRequest
import io.ktor.server.application.ApplicationCall

fun isAdmin(call: ApplicationCall): Boolean = true

fun validateCreateMovieRequest(request: CreateMovieRequest): String? =
    validateMovieFields(
        title = request.title,
        description = request.description,
        genre = request.genre,
        year = request.year,
        rating = request.rating
    )

fun validateUpdateMovieRequest(request: UpdateMovieRequest): String? =
    validateMovieFields(
        title = request.title,
        description = request.description,
        genre = request.genre,
        year = request.year,
        rating = request.rating
    )

private fun validateMovieFields(
    title: String,
    description: String,
    genre: String,
    year: Int,
    rating: Double
): String? {
    return when {
        title.isBlank() -> "Название фильма не должно быть пустым"
        description.isBlank() -> "Описание фильма не должно быть пустым"
        genre.isBlank() -> "Жанр фильма не должен быть пустым"
        year <= 1888 -> "Год выпуска должен быть больше 1888"
        rating < 0.0 || rating > 10.0 -> "Рейтинг должен быть от 0.0 до 10.0"
        else -> null
    }
}
