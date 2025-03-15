package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.repository.stores.LikesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesMapper
import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.LikesRepository
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LikesRepositoryImpl @Inject constructor(
    private val movieLocalStore: LikesLocalStore,
) : LikesRepository {


    override fun observe(): Flow<List<Int>> {
        return movieLocalStore.observeLikedMoviesIds()
    }

    override suspend fun add(movieId: Int) {
        movieLocalStore.likeMovie(movieId)
    }

    override suspend fun remove(movieId: Int) {
        movieLocalStore.dislikeMovie(movieId)
    }
}