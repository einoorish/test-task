package app.bettermetesttask.movies.sections

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.featurecommon.utils.images.GlideApp
import app.bettermetesttask.movies.R
import app.bettermetesttask.movies.databinding.MovieDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MovieDetailBottomSheet @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private lateinit var binding: MovieDetailsBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MovieDetailBottomSheet>

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)

        val view = LayoutInflater.from(context).inflate(R.layout.movie_details, this, true)
        binding = MovieDetailsBinding.bind(view)

        bottomSheetBehavior = BottomSheetBehavior.from(this).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    fun show(item: Movie, onLikeClicked: (Movie) -> Unit){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        with(binding) {
            titleTv.text = item.title
            descriptionTv.text = item.description
            GlideApp.with(binding.rootView)
                .load(item.posterPath)
                .into(posterIv)
            btnLike.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.rootView.context,
                    if (item.liked) {
                        R.drawable.ic_favorite_liked
                    } else {
                        R.drawable.ic_favorite_not_liked
                    }
                )
            )
            btnLike.setOnClickListener {
                onLikeClicked.invoke(item)
            }
        }
    }

    fun hide() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}
