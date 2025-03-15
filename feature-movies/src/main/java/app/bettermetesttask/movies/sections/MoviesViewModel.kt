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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
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
            observeMoviesUseCase().map {
                when {
                    it is Success && it.data.isNotEmpty() -> Loaded(it.data)
                    else -> LoadedEmpty
                }
            }.onEach { state ->
                Timber.d("Movies state updated: $state")
                _movies.tryEmit(state)
            }.take(1).collect()
        }
    }

    fun switchLikeStatus(movie: Movie) {
        viewModelScope.launch {
            if (movie.liked) {
                likeMovieUseCase(movie.id)
            } else {
                dislikeMovieUseCase(movie.id)
            }
        }
    }

    fun openMovieDetails(movie: Movie) {
        Timber.d("Requested to open movie details for $movie")
        _selectedMovie.tryEmit(Visible(movie))
    }

}