package app.bettermetesttask.domainmovies.repository

import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie

interface MoviesRepository {

    suspend fun getAll(): Result<List<Movie>>

    suspend fun get(id: Int): Result<Movie>

}
