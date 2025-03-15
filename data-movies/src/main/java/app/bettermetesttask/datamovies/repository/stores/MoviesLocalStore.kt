package app.bettermetesttask.datamovies.repository.stores

import android.database.sqlite.SQLiteException
import app.bettermetesttask.datamovies.database.MoviesDatabase
import app.bettermetesttask.datamovies.database.dao.MoviesDao
import app.bettermetesttask.datamovies.database.entities.MovieEntity
import javax.inject.Inject

class MoviesLocalStore @Inject constructor(
    private val database: MoviesDatabase
) {

    private val moviesDao: MoviesDao
        get() = database.getMoviesDao()

    @Throws(SQLiteException::class)
    suspend fun getAll(): List<MovieEntity> {
        return moviesDao.selectMovies()
    }

    @Throws(NoSuchElementException::class)
    suspend fun get(id: Int): MovieEntity {
        return moviesDao.selectMovieById(id).first()
    }

    @Throws(SQLiteException::class)
    suspend fun insert(movie: MovieEntity) {
        moviesDao.insertMovie(movie)
    }

    @Throws(SQLiteException::class)
    suspend fun insertAll(movies: List<MovieEntity>) {
        moviesDao.insertMovies(movies)
    }

}