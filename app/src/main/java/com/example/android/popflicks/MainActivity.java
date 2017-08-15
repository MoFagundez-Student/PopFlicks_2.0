package com.example.android.popflicks;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popflicks.data.MovieLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popflicks.utils.NetworkUtils.networkConnectionIsNull;

/**
 * Pop Flicks
 * Created by Mauricio on July 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 1
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>, MovieAdapter.MovieAdapterOnClickHandler {

    /**
     * Member variables
     */
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    @BindView(R.id.container_empty_state) LinearLayout mLayoutEmptyView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    private String mQueryFilter;
    @BindView(R.id.text_view_error_message) TextView mErrorMessageTextView;
    private boolean mIsShowingFavouriteMovie = false;

    /**
     * Log tag constant used with error messages
     */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Constant used to send {@link android.os.Parcelable} to {@link DetailActivity}
     */
    public static final String PARCELABLE_KEY = "movie";

    /**
     * Constant for the LoaderManager
     */
    private static final int LOADERMANAGER_INSTANCE = 0;

    /**
     * Constants used to define type of query being made:
     * <p>
     * Most popular movies; or
     * Top rated movies; or
     * Favourite movies.
     */
    public static final String QUERY_POPULAR = "popular?api_key=";
    public static final String QUERY_TOP_RATED = "top_rated?api_key=";
    public static final String QUERY_REVIEWS = "/reviews?api_key=";
    public static final String QUERY_TRAILERS = "/videos?api_key=";
    public static final String QUERY_FAVOURITES = "favourites";
    public static final String QUERY_TYPE = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views with ButterKnife
        ButterKnife.bind(this);

        // Set a GridLayoutManager to the RecyclerView
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, numberOfColumns());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set fix size to the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Kick off LoaderManager
        getLoaderManager().initLoader(LOADERMANAGER_INSTANCE, null, MainActivity.this);

    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        // Show ProgressBar
        mProgressBar.setVisibility(View.VISIBLE);
        // Check if user has made a filter selection/query filter
        if (args == null) {
            // Bundle is null so we assume the app is starting, so default behaviour
            // is to load top rated movies
            mQueryFilter = QUERY_TOP_RATED;
        } else {
            // If user has made a selection, MovieLoader will load the correct URL from Bundle
            mQueryFilter = args.getString(QUERY_TYPE);
        }
        // Return new MovieLoader
        return new MovieLoader(this, mQueryFilter);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        // Show movie data in the UI if data is not null
        if (null != data && data.size() > 0) {
            showMovieData();
            // Set MovieAdapter to properly display data into the RecyclerView
            mMovieAdapter = new MovieAdapter(data, this, this);
            mRecyclerView.setAdapter(mMovieAdapter);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mRecyclerView.setAdapter(null);
    }

    /**
     * Handle {@link RecyclerView} item clicks
     */
    @Override
    public void onClick(Movie movie) {
        if (!networkConnectionIsNull(this) || mIsShowingFavouriteMovie) {
            // Create new intent
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            // Put movie information into the intent
            intent.putExtra(PARCELABLE_KEY, new Movie(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getSynopsis(),
                    movie.getRating(),
                    movie.getReleaseDate(),
                    movie.getThumbnail(),
                    movie.getImageBlob()
            ));
            // Start new DetailActivity
            this.startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.error_empty_state_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable("SAVE", mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("SAVE");
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate correct menu from XML
        getMenuInflater().inflate(R.menu.sort_criteria_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = new Bundle();
        // Check which menu item is being selected
        switch (item.getItemId()) {
            // If filter criteria is most popular:
            case R.id.criteria_most_popular:
                // Pass query type as most popular so LoaderManager can load the correct URL
                bundle.putString(QUERY_TYPE, QUERY_POPULAR);
                mIsShowingFavouriteMovie = false;
                break;
            case R.id.criteria_top_rated:
                // Pass query type as top rated so LoaderManager can load the correct URL
                bundle.putString(QUERY_TYPE, QUERY_TOP_RATED);
                mIsShowingFavouriteMovie = false;
                break;
            default:
                // Pass query type as favourites so ContentProvider can load from database
                bundle.putString(QUERY_TYPE, QUERY_FAVOURITES);
                mIsShowingFavouriteMovie = true;
                break;
        }
        // Restart LoaderManager to show new data in the screen
        getLoaderManager().restartLoader(LOADERMANAGER_INSTANCE, bundle, MainActivity.this);
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method will make the error message visible and hide the data shown in the UI
     */
    private void showErrorMessage() {
        // Hide visible data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Set error message text to 'network error' (default)
        mErrorMessageTextView.setText(getString(R.string.error_empty_state_network));
        if (mIsShowingFavouriteMovie) {
            // If query is being filtered by favourites, then set error message to 'empty favourites'
            mErrorMessageTextView.setText(getString(R.string.error_empty_state_favourites));
        }
        // Show error message
        mLayoutEmptyView.setVisibility(View.VISIBLE);
        // Hide ProgressBar
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * This method will make the error message invisible and show the data shown in the UI
     */
    private void showMovieData() {
        // Hide error message
        mLayoutEmptyView.setVisibility(View.INVISIBLE);
        // Show visible data
        mRecyclerView.setVisibility(View.VISIBLE);
        // Hide ProgressBar
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Dynamically calculate the number of columns and the layout to adapt to the screen size and orientation
     * */
    private int numberOfColumns() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int widthDivider = 350;
            int width = displayMetrics.widthPixels;
            int nColumns = width / widthDivider;
            if (nColumns >= 3) return 3;
            if (nColumns <= 2) return 2;
        } else {
            return 2;
        }
        return 2;
    }

}