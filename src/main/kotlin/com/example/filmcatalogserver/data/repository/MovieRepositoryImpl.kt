package com.example.filmcatalogserver.data.repository

import com.example.filmcatalogserver.data.dto.CreateMovieRequest
import com.example.filmcatalogserver.data.dto.UpdateMovieRequest
import com.example.filmcatalogserver.data.table.MoviesTable
import com.example.filmcatalogserver.data.table.WatchlistTable
import com.example.filmcatalogserver.domain.model.Movie
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class MovieRepositoryImpl : MovieRepository {
    override suspend fun getAll(): List<Movie> = dbQuery {
        MoviesTable
            .selectAll()
            .orderBy(MoviesTable.movieId)
            .map { it.toMovie() }
    }

    override suspend fun getFiltered(genre: String?, year: Int?): List<Movie> = dbQuery {
        val normalizedGenre = genre?.trim()?.takeIf { it.isNotBlank() }
        val query = when {
            normalizedGenre != null && year != null -> MoviesTable
                .selectAll()
                .where {
                    (MoviesTable.genre.lowerCase() eq normalizedGenre.lowercase()) and
                        (MoviesTable.year eq year)
                }

            normalizedGenre != null -> MoviesTable
                .selectAll()
                .where { MoviesTable.genre.lowerCase() eq normalizedGenre.lowercase() }

            year != null -> MoviesTable
                .selectAll()
                .where { MoviesTable.year eq year }

            else -> MoviesTable.selectAll()
        }

        query
            .orderBy(MoviesTable.movieId)
            .map { it.toMovie() }
    }

    override suspend fun getById(id: Int): Movie? = dbQuery {
        MoviesTable
            .selectAll()
            .where { MoviesTable.movieId eq id }
            .singleOrNull()
            ?.toMovie()
    }

    override suspend fun searchByTitle(query: String): List<Movie> = dbQuery {
        val pattern = "%${query.lowercase()}%"
        MoviesTable
            .selectAll()
            .where { MoviesTable.title.lowerCase() like pattern }
            .orderBy(MoviesTable.movieId)
            .map { it.toMovie() }
    }

    override suspend fun getGenres(): List<String> = dbQuery {
        MoviesTable
            .selectAll()
            .map { it[MoviesTable.genre] }
            .distinct()
            .sorted()
    }

    override suspend fun getYears(): List<Int> = dbQuery {
        MoviesTable
            .selectAll()
            .map { it[MoviesTable.year] }
            .distinct()
            .sortedDescending()
    }

    override suspend fun create(request: CreateMovieRequest): Movie = dbQuery {
        val id = MoviesTable.insert {
            it[title] = request.title.trim()
            it[description] = request.description.trim()
            it[year] = request.year
            it[genre] = request.genre.trim()
            it[rating] = request.rating
            it[imageUrl] = request.imageUrl.trim()
        }[MoviesTable.movieId]

        MoviesTable
            .selectAll()
            .where { MoviesTable.movieId eq id }
            .single()
            .toMovie()
    }

    override suspend fun update(id: Int, request: UpdateMovieRequest): Movie? = dbQuery {
        val updatedRows = MoviesTable.update({ MoviesTable.movieId eq id }) {
            it[title] = request.title.trim()
            it[description] = request.description.trim()
            it[year] = request.year
            it[genre] = request.genre.trim()
            it[rating] = request.rating
            it[imageUrl] = request.imageUrl.trim()
        }

        if (updatedRows == 0) {
            null
        } else {
            MoviesTable
                .selectAll()
                .where { MoviesTable.movieId eq id }
                .single()
                .toMovie()
        }
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        val movieExists = MoviesTable
            .selectAll()
            .where { MoviesTable.movieId eq id }
            .any()

        if (!movieExists) {
            false
        } else {
            WatchlistTable.deleteWhere { movieId eq id }
            MoviesTable.deleteWhere { movieId eq id }
            true
        }
    }

    private fun ResultRow.toMovie(): Movie =
        Movie(
            id = this[MoviesTable.movieId],
            title = this[MoviesTable.title],
            description = this[MoviesTable.description],
            year = this[MoviesTable.year],
            genre = this[MoviesTable.genre],
            rating = this[MoviesTable.rating],
            imageUrl = this[MoviesTable.imageUrl]
        )

    private suspend fun <T> dbQuery(block: () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
