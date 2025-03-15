package app.bettermetesttask.domainmovies.repository

import kotlinx.coroutines.flow.Flow

interface LikesRepository {

    fun observe(): Flow<List<Int>>

    suspend fun add(movieId: Int)

    suspend fun remove(movieId: Int)
}
