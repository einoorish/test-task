package app.bettermetesttask.movies.sections

import app.bettermetesttask.domainmovies.entries.Movie

sealed class MovieDetailsState {

    data object Hidden : MovieDetailsState()

    data class Visible(val movie: Movie) : MovieDetailsState()

}