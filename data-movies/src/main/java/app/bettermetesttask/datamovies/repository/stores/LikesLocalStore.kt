package app.bettermetesttask.datamovies.repository.stores

import app.bettermetesttask.datamovies.database.MoviesDatabase
import app.bettermetesttask.datamovies.database.dao.LikesDao
import app.bettermetesttask.datamovies.database.entities.LikedMovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LikesLocalStore @Inject constructor(
    private val database: MoviesDatabase
) {

    private val dao: LikesDao
        get() = database.getLikesDao()

    suspend fun likeMovie(id: Int) {
        dao.insertLikedEntry(LikedMovieEntity(id))
    }

    suspend fun dislikeMovie(id: Int) {
        dao.removeLikedEntry(id)
    }

    fun observeLikedMoviesIds(): Flow<List<Int>> {
        return dao.selectLikedEntries().map { movieIdsFlow -> movieIdsFlow.map { it.movieId } }
    }
}