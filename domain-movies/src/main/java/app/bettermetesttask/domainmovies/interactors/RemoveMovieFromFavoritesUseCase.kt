package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domainmovies.repository.LikesRepository
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import javax.inject.Inject

class RemoveMovieFromFavoritesUseCase @Inject constructor(
    private val repository: LikesRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.remove(id)
    }
}