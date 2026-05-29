package com.example.filmcatalogserver.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val description: String,
    val year: Int,
    val genre: String,
    val rating: Double,
    val imageUrl: String
)
