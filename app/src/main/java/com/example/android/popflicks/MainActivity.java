package com.example.android.popflicks;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popflicks.data.MovieLoader;

import java.util.List;

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
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private LinearLayout mLayoutEmptyView;
    private ProgressBar mProgressBar;
    private String meQueryFilter;

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
     * Top rated movies.
     */
    public static final String QUERY_POPULAR = "popular?";
    public static final String QUERY_TOP_RATED = "top_rated?";
    public static final String QUERY_TYPE = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a layout reference for the ProgressBar and pass to a variable
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Find a layout reference for the RecyclerView and pass to a variable
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Find a layout reference for the empty view container and pass to a variable
        mLayoutEmptyView = (LinearLayout) findViewById(R.id.container_empty_state);

        // Set a GridLayoutManager to the RecyclerView
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set fix size to the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Check if network connection exist and properly update the UI
        if (!networkConnectionIsNull(this)) {
            // Kick off LoaderManager
            getLoaderManager().initLoader(LOADERMANAGER_INSTANCE, null, MainActivity.this);
        } else {
            // Display error message
            showErrorMessage();
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        // Show ProgressBar
        mProgressBar.setVisibility(View.VISIBLE);
        // Check if user has made a filter selection/query filter
        if (args == null) {
            // Bundle is null so we assume the app is starting, so default behaviour
            // is to load top rated movies
            meQueryFilter = QUERY_TOP_RATED;
        } else {
            // If user has made a selection, MovieLoader will load the correct URL from Bundle
            meQueryFilter = args.getString(QUERY_TYPE);
        }
        //return new MovieLoader
        return new MovieLoader(this, meQueryFilter);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        // Show movie data in the UI
        showMovieData();
        // Set MovieAdapter to properly display data into the RecyclerView
        mMovieAdapter = new MovieAdapter(data, this, this);
        mRecyclerView.setAdapter(mMovieAdapter);
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
        if (!networkConnectionIsNull(this)) {
            // Create new intent
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            // Put movie information into the intent
            intent.putExtra(PARCELABLE_KEY, new Movie(
                    movie.getTitle(),
                    movie.getSynopsis(),
                    movie.getRating(),
                    movie.getReleaseDate(),
                    movie.getThumbnail()));
            // Start new DetailActivity
            this.startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.error_empty_state), Toast.LENGTH_SHORT).show();
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
        if (!networkConnectionIsNull(this)) {
            // Create a new Bundle
            Bundle bundle = new Bundle();
            // Check which menu item is being selected
            switch (item.getItemId()) {
                // If filter criteria is most popular:
                case R.id.criteria_most_popular:
                    // Pass query type as most popular so LoaderManager can load the correct URL
                    bundle.putString(QUERY_TYPE, QUERY_POPULAR);
                    break;
                default:
                    // Pass query type as top rated so LoaderManager can load the correct URL
                    bundle.putString(QUERY_TYPE, QUERY_TOP_RATED);
                    break;
            }
            // Restart LoaderManager to show new data in the screen
            getLoaderManager().restartLoader(LOADERMANAGER_INSTANCE, bundle, MainActivity.this);
        } else {
            showErrorMessage();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check whether or not network connectivity is available and return boolean value
     */
    public static boolean networkConnectionIsNull(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return !(activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    /**
     * This method will make the error message visible and hide the data shown in the UI
     */
    private void showErrorMessage() {
        // Hide visible data
        mRecyclerView.setVisibility(View.INVISIBLE);
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

}