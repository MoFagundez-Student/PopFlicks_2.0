package com.example.android.popflicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popflicks.MainActivity.PARCELABLE_KEY;

public class DetailActivity extends AppCompatActivity {

    /**
     * Using ButterKnife to automatically finds each field by the specified ID.
     */
    @BindView(R.id.text_view_title)         TextView mTitle;          // Original movie title
    @BindView(R.id.text_view_synopsis)      TextView mSynopsis;       // Plot synopsis
    @BindView(R.id.text_view_rating)        TextView mRating;         // User rating from the community
    @BindView(R.id.text_view_release_date)  TextView mReleaseDate;    // Movie release date
    @BindView(R.id.image_view_detail)       ImageView mThumbnail;     // Poster image thumbnail
    private Movie mMovie;

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
     * Load movie information from {@link Movie} object into the previously
     * inflated XML elements
     */
    private void showMovieInfo() {
        // Load movie data into the variables
        mTitle.setText(mMovie.getTitle());
        Picasso.with(this).load(mMovie.getThumbnail())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
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
