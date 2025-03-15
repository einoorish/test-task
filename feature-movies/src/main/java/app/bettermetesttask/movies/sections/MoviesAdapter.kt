package app.bettermetesttask.movies.sections

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.featurecommon.utils.images.GlideApp
import app.bettermetesttask.movies.R
import app.bettermetesttask.movies.databinding.MovieItemBinding
import timber.log.Timber
import javax.inject.Inject


class MoviesAdapter @Inject constructor() : ListAdapter<Movie, MoviesAdapter.MoviesHolder>(MovieItemDiffCallback()) {

    var onItemClicked: ((movie: Movie) -> Unit)? = null
    var onItemLiked: ((movie: Movie) -> Unit)? = null

    private lateinit var itemLayoutParams: LayoutParams

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, COLUMN_COUNT)

        val windowManager = recyclerView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.width()
        } else {
            val metrics = DisplayMetrics()
            windowManager.getDefaultDisplay().getMetrics(metrics)
            metrics.widthPixels
        }

        val itemWidth = displayWidth / COLUMN_COUNT
        val itemHeight = itemWidth * 3/2
        itemLayoutParams = LayoutParams(itemWidth, itemHeight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoviesHolder(binding).apply {
            this.itemView.layoutParams = itemLayoutParams
        }
    }

    override fun onBindViewHolder(holder: MoviesHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MoviesHolder(private val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Movie) {
            with(binding) {
                GlideApp.with(root.context)
                    .load(item.posterPath)
                    .into(posterIv)
                btnLike.setImageDrawable(
                    ContextCompat.getDrawable(
                        root.context,
                        when {
                            item.liked -> R.drawable.ic_favorite_liked
                            else -> R.drawable.ic_favorite_not_liked
                        }
                    )
                )
                btnLike.setOnClickListener {
                    onItemLiked?.invoke(item)
                }
                posterIv.setOnClickListener {
                    onItemClicked?.invoke(item)
                }
            }
        }
    }

    companion object {
        const val COLUMN_COUNT = 3
    }
}

class MovieItemDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}
