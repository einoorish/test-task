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
interface LikesDao{

    @Query("SELECT * FROM LikedMovieEntry")
    fun selectLikedEntries(): Flow<List<LikedMovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedEntry(entry: LikedMovieEntity)

    @Query("DELETE FROM LikedMovieEntry WHERE movie_id = :movieId")
    suspend fun removeLikedEntry(movieId: Int)

}