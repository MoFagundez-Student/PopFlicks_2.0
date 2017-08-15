package com.example.android.popflicks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popflicks.data.MovieContract.MovieEntry;
import com.example.android.popflicks.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popflicks.MainActivity.LOG_TAG;
import static com.example.android.popflicks.MainActivity.PARCELABLE_KEY;
import static com.example.android.popflicks.MainActivity.QUERY_REVIEWS;
import static com.example.android.popflicks.MainActivity.QUERY_TRAILERS;
import static com.example.android.popflicks.utils.NetworkUtils.createUrl;
import static com.example.android.popflicks.utils.NetworkUtils.extractReviewsFromJson;
import static com.example.android.popflicks.utils.NetworkUtils.extractTrailersFromJson;
import static com.example.android.popflicks.utils.NetworkUtils.makeHttpRequest;
import static com.example.android.popflicks.utils.NetworkUtils.networkConnectionIsNull;

public class DetailActivity extends AppCompatActivity {

    /**
     * Using ButterKnife to automatically find each field by the specified ID.
     */
    private static final int PARAMS_ZERO_POSITION = 0;
    private static final int ARRAY_LENGTH = 2;
    private static final int ARRAY_POSITION_TRAILERS_JSON = 0;
    private static final int ARRAY_POSITION_REVIEWS_JSON = 1;

    @BindView(R.id.text_view_title)         TextView mTitle;          // Original movie title
    @BindView(R.id.text_view_synopsis)      TextView mSynopsis;       // Plot synopsis
    @BindView(R.id.text_view_rating)        TextView mRating;         // User rating from the community
    @BindView(R.id.text_view_release_date)  TextView mReleaseDate;    // Movie release date
    @BindView(R.id.image_view_detail)       ImageView mThumbnail;     // Poster image thumbnail
    @BindView(R.id.label_reviews)           TextView mLabelReviews;   // Label
    @BindView(R.id.label_trailers)          TextView mLabelTrailers;   // Label
    @BindView(R.id.layout_reviews)       LinearLayout mLayoutReviews; // LinearLayout with movie reviews
    @BindView(R.id.layout_trailers)      LinearLayout mLayoutTrailers; // LinearLayout with movie trailers
    private Menu mMenu;

    private Movie mMovie;
    private List<Review> mReviews;
    private List<Trailer> mTrailers;
    private boolean movieIsFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Bind views with ButterKnife
        ButterKnife.bind(this);

        // Get intent sent from previous activity
        Intent intent = getIntent();
        // Check if intent contains any data
        if (intent.hasExtra(PARCELABLE_KEY)) {
            // Assign the intent content to movie object
            mMovie = intent.getParcelableExtra(PARCELABLE_KEY);
        }

        // Kick off AsyncTask to load trailers and reviews
        new FetchTrailerAndReviewPaths().execute(mMovie.getId());
    }

    /** Class used to retrieve trailers and reviews from the specific movie being shown in this activity */
    private class FetchTrailerAndReviewPaths extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            // Define URLs to be queried
            URL queryUrlTrailers = createUrl(QUERY_TRAILERS, params[PARAMS_ZERO_POSITION]);
            URL queryUrlReviews = createUrl(QUERY_REVIEWS, params[PARAMS_ZERO_POSITION]);
            // Initialise array to store responses
            String[] jsonResponse = new String[ARRAY_LENGTH];
            try {
                // Fetch JSON for the Trailers
                jsonResponse[ARRAY_POSITION_TRAILERS_JSON] = makeHttpRequest(queryUrlTrailers);
                // Fetch JSON for the Reviews
                jsonResponse[ARRAY_POSITION_REVIEWS_JSON] = makeHttpRequest(queryUrlReviews);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Exception thrown:" + e.toString());
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String... s) {
            super.onPostExecute(s);
            // Initialise arrays
            mReviews = new ArrayList<>();
            mTrailers = new ArrayList<>();
            // Parse information from JSON and assign values to List
            mReviews = extractReviewsFromJson(s[ARRAY_POSITION_REVIEWS_JSON]);
            mTrailers = extractTrailersFromJson(s[ARRAY_POSITION_TRAILERS_JSON]);
            showMovieInfo();
        }
    }

    private class CheckMovieIsFavourite extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // Define movie Uri to be queried
            Uri movieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, Long.valueOf(mMovie.getId()));
            // Query the database and return a Cursor
            Cursor cursor = getApplicationContext().getContentResolver().query(movieUri, null, null, null, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            movieIsFavourite = aBoolean;
            changeStarIfMoveFavourite();
        }
    }

    /**
     * Load movie information from {@link Movie} object into the previously
     * inflated XML elements
     */
    private void showMovieInfo() {
        // Load movie data into the variables
        mTitle.setText(mMovie.getTitle());

        // Load movie poster
        if (null != mMovie.getImageBlob()) {
            // Convert from byte to bitmap and inflate the image
            Bitmap bitmap = BitmapFactory.decodeByteArray(mMovie.getImageBlob(), 0, mMovie.getImageBlob().length);
            mThumbnail.setImageBitmap(bitmap);
        } else if (null != mMovie.getThumbnail()) {
            // Load from web with Picasso
            Picasso.with(this).load(mMovie.getThumbnail())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(mThumbnail);
        } else {
            // Show placeholder in case of no image available
            mThumbnail.setImageResource(R.drawable.ic_placeholder);
        }

        // Convert release date and show in the UI
        mReleaseDate.setText(Utils.convertDateToYear(mMovie.getReleaseDate()));

        // Load movie rating
        mRating.setText(mMovie.getRating() + getResources().getString(R.string.user_rating_max));

        // Load movie synopsis
        mSynopsis.setText(mMovie.getSynopsis());

        // Show reviews and trailers if internet connection is available
        if (!networkConnectionIsNull(this)) {
            // Check if the movie has reviews parsed from JSON earlier
            if (null != mReviews) {
                showReviews();
            }

            // Check if the movie has trailers parsed from JSON earlier
            if (null != mTrailers) {
                showTrailers();
            }
        }

        // Query the database to know if it's a favourite movie or not
        new CheckMovieIsFavourite().execute(mMovie.getId());
    }

    /** Method used to display movie reviews in the UI */
    private void showReviews() {
        // Show label
        mLabelReviews.setVisibility(View.VISIBLE);
        // Create adapter
        ArrayList<Review> reviewsArrayList = (ArrayList<Review>) mReviews;
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, R.layout.review_list_item, reviewsArrayList);
        // Adding reviews to the LinearLayout
        final int reviewAdapterCount = reviewAdapter.getCount();
        for (int i = 0; i < reviewAdapterCount; i++) {
            View item = reviewAdapter.getView(i, null, null);
            mLayoutReviews.addView(item);
        }
    }

    /** Method used to display movie trailers in the UI */
    private void showTrailers() {
        // Show the label
        mLabelTrailers.setVisibility(View.VISIBLE);
        // Loop through mTrailers to create clickable views
        for (int i = 0; i < mTrailers.size(); i++) {
            // Define LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Inflate view
            View trailerLayout = inflater.inflate(R.layout.trailer_list_item, null);
            // Set name for the trailer
            TextView trailerName = (TextView) trailerLayout.findViewById(R.id.text_view_trailer_name);
            trailerName.setText(mTrailers.get(i).getmName());
            // Add view to the parent Layout to be displayed as a list
            mLayoutTrailers.addView(trailerLayout);
            // Define view position inside parent to be set as Tag
            int position = mLayoutTrailers.indexOfChild(trailerLayout);
            trailerLayout.setTag(position);

            // Set click listener to each individual list item created inside the parent layout
            trailerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get position defined previously
                    int position = (Integer) v.getTag();
                    // Send an intent to show movie trailer
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mTrailers.get(position).getTrailerUrl()));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate correct menu from XML
        getMenuInflater().inflate(R.menu.add_favourite_menu, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_remove_favourite:
                if (movieIsFavourite) {
                    deleteMovieFromFavourites(mMovie);
                    movieIsFavourite = false;
                } else {
                    addMovieToFavourites(mMovie);
                    movieIsFavourite = true;
                }
                changeStarIfMoveFavourite();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /** Create a {@link ContentValues} object and perform an insert action into the database */
    private void addMovieToFavourites(Movie movie) {
        // Create content value object and store values in it
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_MDB_ID, movie.getId());
        values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());
        values.put(MovieEntry.COLUMN_RATING, movie.getRating());
        values.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        // Transform movie poster drawable into a bitmap
        //byte[] image = getBytes(mThumbnail.getDrawable());
        values.put(MovieEntry.COLUMN_IMAGE_BLOB, movie.getImageBlob());

        // Insert a new movie into de database
        Uri newUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        // Inform user of the successful product insertion
        Toast.makeText(this, getString(R.string.movie_added_favourite), Toast.LENGTH_SHORT).show();
    }

    /** Delete movie record from database */
    private void deleteMovieFromFavourites(Movie movie) {
        // Define movie Uri to be deleted
        Uri movieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, Long.valueOf(movie.getId()));
        //  Perform db deletion
        int rowsDeleted = getContentResolver().delete(movieUri, null, null);
        Log.i(LOG_TAG, String.valueOf(rowsDeleted));
        // Inform user of the successful deletion
        Toast.makeText(this, getString(R.string.movie_removed_favourite), Toast.LENGTH_SHORT).show();
    }

    /** Change star icon (favourite) accordingly if the movie is in the favourite db or not */
    private void changeStarIfMoveFavourite() {
        if (movieIsFavourite) {
            mMenu.getItem(0).setIcon(R.drawable.ic_favourite_on);
        } else {
            mMenu.getItem(0).setIcon(R.drawable.ic_favourite_off);
        }
    }

}
