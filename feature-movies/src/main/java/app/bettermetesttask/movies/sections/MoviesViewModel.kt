package app.bettermetesttask.movies.sections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bettermetesttask.domaincore.utils.Result.Success
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.movies.sections.MoviesState.*
import app.bettermetesttask.movies.sections.MovieDetailsState.*
import app.bettermetesttask.domainmovies.interactors.AddMovieToFavoritesUseCase
import app.bettermetesttask.domainmovies.interactors.ObserveMoviesUseCase
import app.bettermetesttask.domainmovies.interactors.RemoveMovieFromFavoritesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MoviesViewModel @Inject constructor(
    private val observeMoviesUseCase: ObserveMoviesUseCase,
    private val likeMovieUseCase: AddMovieToFavoritesUseCase,
    private val dislikeMovieUseCase: RemoveMovieFromFavoritesUseCase,
) : ViewModel() {

    private val _movies: MutableStateFlow<MoviesState> = MutableStateFlow(Initial)
    private val _selectedMovie: MutableStateFlow<MovieDetailsState> = MutableStateFlow(Hidden)

    val movies: StateFlow<MoviesState>
        get() = _movies.asStateFlow()

    val selectedMovie: StateFlow<MovieDetailsState>
        get() = _selectedMovie.asStateFlow()

    fun updateMovies() {
        _movies.tryEmit(Loading)
        viewModelScope.launch {
            observeMoviesUseCase().collect {
                if(it is Success && it.data.isNotEmpty()) {
                    _movies.value = Loaded(it.data)
                    updateSelectedMovieInfo(it.data)
                } else {
                    _movies.value = LoadedEmpty
                }
            }
        }
    }

    private fun updateSelectedMovieInfo(movies: List<Movie>){
        val oldData = (_selectedMovie.value as? Visible) ?: return
        _selectedMovie.value = movies.firstOrNull { it.id ==  oldData.movie.id}
            ?.let { Visible(it) } ?: Hidden
    }

    fun switchLikeStatus(movie: Movie) {
        viewModelScope.launch {
            if (movie.liked) {
                dislikeMovieUseCase(movie.id)
            } else {
                likeMovieUseCase(movie.id)
            }
        }
    }

    fun openMovieDetails(movie: Movie) {
        Timber.d("Requested to open movie details for $movie")
        _selectedMovie.value = Visible(movie)
    }

}