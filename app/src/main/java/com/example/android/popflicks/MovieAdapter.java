package com.example.android.popflicks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popflicks.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Pop Flicks
 * Created by Mauricio on July 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 1
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMoviesData;
    private Context mContext;
    private final MovieAdapterOnClickHandler mClickHandler; // ClickHandler used to implement the RecyclerView click listener

    public MovieAdapter(List<Movie> mMoviesArray, Context context, MovieAdapterOnClickHandler clickHandler) {
        this.mMoviesData = mMoviesArray;
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView is laid out.
     * Enough ViewHolders will be created to fill the screen and allow for scrolling.
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false));
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     */
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        // Load movie poster
        if (null != mMoviesData.get(position).getImageBlob()) {
            // Convert from byte to bitmap and inflate the image
            Bitmap bitmap = BitmapFactory.decodeByteArray(mMoviesData.get(position).getImageBlob(), 0, mMoviesData.get(position).getImageBlob().length);
            holder.mThumbnailImageView.setImageBitmap(bitmap);
        } else if (null != mMoviesData.get(position).getThumbnail()) {
            // Load from web with Picasso
            Picasso.with(mContext).load(mMoviesData.get(position).getThumbnail())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(holder.mThumbnailImageView);
        } else {
            // Show placeholder in case of no image available
            holder.mThumbnailImageView.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) return 0;
        return mMoviesData.size();
    }

    /**
     * Cache of children views
     * */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mThumbnailImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            // Find the XML object for the thumbnail and pass into the correct variable
            mThumbnailImageView = (ImageView) itemView.findViewById(R.id.image_view_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Get adapter position
            int adapterPosition = getAdapterPosition();
            Movie movie = mMoviesData.get(adapterPosition);
            movie.setImageBlob(Utils.getBytes(mThumbnailImageView.getDrawable()));
            mClickHandler.onClick(movie);
        }
    }

    /** Helper method to swap data in the adapter */
    public void swapData(List<Movie> movies) {
        this.mMoviesData = movies;
        notifyDataSetChanged();
    }

}
