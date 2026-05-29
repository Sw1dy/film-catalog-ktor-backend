package com.example.filmcatalogserver.data.dto

import com.example.filmcatalogserver.domain.model.Movie
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    val description: String,
    val year: Int,
    val genre: String,
    val rating: Double,
    val imageUrl: String
)

fun Movie.toDto(): MovieDto =
    MovieDto(
        id = id,
        title = title,
        description = description,
        year = year,
        genre = genre,
        rating = rating,
        imageUrl = imageUrl
    )
