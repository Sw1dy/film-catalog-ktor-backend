package com.example.filmcatalogserver.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateMovieRequest(
    val title: String,
    val description: String,
    val year: Int,
    val genre: String,
    val rating: Double,
    val imageUrl: String
)
