package com.example.android.popflicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.android.popflicks.MainActivity.PARCELABLE_KEY;

public class DetailActivity extends AppCompatActivity {

    private TextView mTitle;          // Original movie title
    private TextView mSynopsis;       // Plot synopsis
    private TextView mRating;         // User rating from the community
    private TextView mReleaseDate;    // Movie release date
    private ImageView mThumbnail;     // Poster image thumbnail
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get intent sent from previous activity
        Intent intent = getIntent();
        // Assign the intent content to movie object
        mMovie = intent.getParcelableExtra(PARCELABLE_KEY);
        // Populate the variables with XML elements
        displayUI();

        // Check connectivity
        if (!MainActivity.networkConnectionIsNull(this)) {
            // Show movie data to the user
            showMovieInfo();
        } else {
            // Hide fields from the user
            hideUI();
        }

    }

    /**
     * Find the XML elements that will be used to display the movie information
     * to the user so they can be manipulated through Java code
     */
    private void displayUI() {
        // Find XML elements and assign to variables
        mTitle = (TextView) findViewById(R.id.text_view_title);
        mThumbnail = (ImageView) findViewById(R.id.image_view_detail);
        mReleaseDate = (TextView) findViewById(R.id.text_view_release_date);
        mRating = (TextView) findViewById(R.id.text_view_rating);
        mSynopsis = (TextView) findViewById(R.id.text_view_synopsis);
    }

    /**
     * Load movie information from {@link Movie} object into the previously
     * inflated XML elements
     */
    private void showMovieInfo() {
        // Load movie data into the variables
        mTitle.setText(mMovie.getTitle());
        Picasso.with(this).load(mMovie.getThumbnail())
                .into(mThumbnail);
        mReleaseDate.setText(mMovie.getReleaseDate());
        mRating.setText(mMovie.getRating() + getResources().getString(R.string.user_rating_max));
        mSynopsis.setText(mMovie.getSynopsis());
    }

    /**
     * Hide the UI in case {@link DetailActivity} gets called. Shouldn't happen, though *scratches chin*
     * */
    private void hideUI() {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout_detail);
        layout.setVisibility(View.INVISIBLE);
    }

}
