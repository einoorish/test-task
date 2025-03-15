package app.bettermetesttask.datamovies.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.bettermetesttask.datamovies.database.entities.LikedMovieEntity
import app.bettermetesttask.datamovies.database.entities.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesDao{

    @Query("SELECT * FROM MoviesTable")
    suspend fun selectMovies(): List<MovieEntity>

    @Query("SELECT * FROM MoviesTable WHERE id = :id")
    suspend fun selectMovieById(id: Int): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: MovieEntity)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("DELETE FROM MoviesTable")
    suspend fun deleteMovies()
}