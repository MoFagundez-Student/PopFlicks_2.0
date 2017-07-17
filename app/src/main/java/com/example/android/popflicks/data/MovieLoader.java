package com.example.android.popflicks.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.popflicks.Movie;
import com.example.android.popflicks.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popflicks.MainActivity.LOG_TAG;

/**
 * Pop Flicks
 * Created by Mauricio on July 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 1
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

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
        // Create URL object
        URL url = NetworkUtils.createUrl(mQueryType);
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
        // Return ArrayList of movies; or null if nothing is retrieved or there's an error
        return movies;
    }

}
