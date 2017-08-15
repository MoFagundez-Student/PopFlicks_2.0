package com.example.android.popflicks.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.android.popflicks.Movie;
import com.example.android.popflicks.data.MovieContract.MovieEntry;
import com.example.android.popflicks.utils.NetworkUtils;
import com.example.android.popflicks.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popflicks.MainActivity.LOG_TAG;
import static com.example.android.popflicks.MainActivity.QUERY_FAVOURITES;

/**
 * Pop Flicks
 * Created by Mauricio on July 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 1
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String DESC_SORT_ORDER = " DESC";
    private String mQueryType;

    public MovieLoader(Context context, String queryType) {
        super(context);
        this.mQueryType = queryType;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public List<Movie> loadInBackground() {
        List<Movie> movies = new ArrayList<>();

        if (mQueryType.equals(QUERY_FAVOURITES)) {
            // Define projection for the Cursor so that contains all rows from the movie table.
            String projection[] = {
                    MovieEntry._ID,
                    MovieEntry.COLUMN_MDB_ID,
                    MovieEntry.COLUMN_TITLE,
                    MovieEntry.COLUMN_SYNOPSIS,
                    MovieEntry.COLUMN_RATING,
                    MovieEntry.COLUMN_RELEASE_DATE,
                    MovieEntry.COLUMN_IMAGE_BLOB
            };
            // Define sort order
            String sortOrder =
                    MovieEntry._ID + DESC_SORT_ORDER;
            // Query database and store result in a cursor
            Cursor cursor = getContext().getContentResolver().query(
                    MovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
            );
            // Extract values from Cursor and pass to movies list
            movies = Utils.extractFromCursor(cursor);
            cursor.close();

        } else {
            // Create URL object
            URL url = NetworkUtils.createUrl(mQueryType, null);
            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = NetworkUtils.makeHttpRequest(url);
                // Create new ArrayList of movies
                movies = new ArrayList<>();
                // Insert movies parsed from JSON into ArrayList
                movies = NetworkUtils.extractMoviesFromJson(jsonResponse);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Could not load movie in background: " + e.toString());
            }
        }
        // Return ArrayList of movies; or null if nothing is retrieved or there's an error
        return movies;
    }

}
