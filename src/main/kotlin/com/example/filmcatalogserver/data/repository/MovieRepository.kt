package com.example.filmcatalogserver.data.repository

import com.example.filmcatalogserver.data.dto.CreateMovieRequest
import com.example.filmcatalogserver.data.dto.UpdateMovieRequest
import com.example.filmcatalogserver.domain.model.Movie

interface MovieRepository {
    suspend fun getAll(): List<Movie>
    suspend fun getFiltered(genre: String?, year: Int?): List<Movie>
    suspend fun getById(id: Int): Movie?
    suspend fun searchByTitle(query: String): List<Movie>
    suspend fun getGenres(): List<String>
    suspend fun getYears(): List<Int>
    suspend fun create(request: CreateMovieRequest): Movie
    suspend fun update(id: Int, request: UpdateMovieRequest): Movie?
    suspend fun delete(id: Int): Boolean
}
