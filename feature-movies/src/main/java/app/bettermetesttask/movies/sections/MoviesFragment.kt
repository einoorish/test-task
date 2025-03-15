package app.bettermetesttask.movies.sections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import app.bettermetesttask.featurecommon.injection.utils.Injectable
import app.bettermetesttask.featurecommon.injection.viewmodel.SimpleViewModelProviderFactory
import app.bettermetesttask.featurecommon.utils.views.gone
import app.bettermetesttask.featurecommon.utils.views.visible
import app.bettermetesttask.movies.sections.MoviesState.*
import app.bettermetesttask.movies.sections.MovieDetailsState.*
import app.bettermetesttask.movies.R
import app.bettermetesttask.movies.databinding.MoviesFragmentBinding
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class MoviesFragment : Fragment(R.layout.movies_fragment), Injectable {

    @Inject
    lateinit var viewModelProvider: Provider<MoviesViewModel>

    @Inject
    lateinit var adapter: MoviesAdapter

    private lateinit var binding: MoviesFragmentBinding

    private val viewModel by viewModels<MoviesViewModel> { SimpleViewModelProviderFactory(viewModelProvider) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MoviesFragmentBinding.inflate(inflater, container, false)
        return binding.rootLayout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.movies.collect(::renderMoviesState)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.selectedMovie.collect(::renderMovieDetailsState)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.onItemClicked = viewModel::openMovieDetails
        adapter.onItemLiked = viewModel::switchLikeStatus

        binding.moviesRV.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateMovies()
    }

    private fun renderMoviesState(state: MoviesState) {
        with(binding) {
            fallbackIV.gone()
            progressBar.gone()
            moviesRV.gone()
            when (state) {
                Initial -> {}
                Loading -> {
                    progressBar.visible()
                }
                is Loaded -> {
                    adapter.submitList(state.movies)
                    moviesRV.visible()
                }
                is LoadedEmpty -> {
                    adapter.submitList(emptyList())
                    fallbackIV.visible()
                }
            }
        }
    }

    private fun renderMovieDetailsState(state: MovieDetailsState) {
        when (state) {
            Hidden -> {
                binding.movieDetailsBottomSheet.hide()
            }
            is Visible -> {
                binding.movieDetailsBottomSheet.show(
                    item = state.movie,
                    onLikeClicked = viewModel::switchLikeStatus
                )
            }
        }
    }

}