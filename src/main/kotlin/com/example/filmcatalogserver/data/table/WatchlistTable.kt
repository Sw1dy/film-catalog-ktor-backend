package com.example.filmcatalogserver.data.table

import org.jetbrains.exposed.sql.Table

object WatchlistTable : Table("watchlist") {
    val watchlistId = integer("watchlist_id").autoIncrement()
    val userId = integer("user_id").references(UsersTable.id)
    val movieId = integer("movie_id").references(MoviesTable.movieId)
    val addedAt = long("added_at")

    override val primaryKey = PrimaryKey(watchlistId)
}
