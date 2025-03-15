package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.repository.stores.LikesLocalStore
import app.bettermetesttask.domainmovies.repository.LikesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LikesRepositoryImpl @Inject constructor(
    private val localStore: LikesLocalStore,
) : LikesRepository {


    override fun observe(): Flow<List<Int>> {
        return localStore.observeLikedMoviesIds()
    }

    override suspend fun add(movieId: Int) {
        localStore.likeMovie(movieId)
    }

    override suspend fun remove(movieId: Int) {
        localStore.dislikeMovie(movieId)
    }
}