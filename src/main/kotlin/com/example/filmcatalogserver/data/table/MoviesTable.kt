package com.example.filmcatalogserver.data.table

import org.jetbrains.exposed.sql.Table

object MoviesTable : Table("movies") {
    val movieId = integer("movie_id").autoIncrement()
    val title = varchar("title", 255)
    val description = text("description")
    val genre = varchar("genre", 255)
    val year = integer("year")
    val rating = double("rating")
    val imageUrl = text("image_url")

    override val primaryKey = PrimaryKey(movieId)
}
