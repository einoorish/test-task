package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.repository.stores.MoviesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesMapper
import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import timber.log.Timber
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val localStore: MoviesLocalStore,
    private val remoteStore: MoviesRestStore,
    private val mapper: MoviesMapper
) : MoviesRepository {

    override suspend fun getAll(): Result<List<Movie>> {
        return Result.of {
            try {
                remoteStore.getAll().also {
                    localStore.insertAll(it.map(mapper.mapToLocal))
                }
            } catch (e: Exception) {
                localStore.getAll().map(mapper.mapFromLocal)
            }
        }.also { Timber.d("Retrieved movie data: $it") }
    }

    override suspend fun get(id: Int): Result<Movie> {
        return Result.of { mapper.mapFromLocal(localStore.get(id)) }
    }

}