package com.example.android.popflicks.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.popflicks.Movie;
import com.example.android.popflicks.data.MovieLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
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

public final class NetworkUtils {

    /** Constant to define base URL to query database */
    private static final String BASE_URL_QUERY = "https://api.themoviedb.org/3/movie/";

    /** Constant to perform query on the movie thumbnail */
    private static final String BASE_URL_THUMBNAIL = "http://image.tmdb.org/t/p/w342/";

    /** Student API Key */
    private static final String STUDENT_API_KEY = BuildConfig.API_KEY;

    /** Constant fields to parse JSON:
     *      JSON_RESULTS used to retrieve array of results
     *      JSON_TITLE used to retrieve movie title
     *      JSON_SYNOPSIS used to retrieve movie synopsis (called overview in the API)
     *      JSON_RATING used to retrieve user ratings (called vote average in the API)
     *      JSON_RELEASE_DATE used to retrieve release date
     *      JSON_THUMBNAIL used to retrieve thumbnail path to be loaded with Picasso API
     *      JSON_NULL_RESULT used there is no result to be parsed from JSON for the movie
     */
    private static final String JSON_RESULTS = "results";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SYNOPSIS = "overview";
    private static final String JSON_RATING = "vote_average";
    private static final String JSON_RELEASE_DATE = "release_date";
    private static final String JSON_THUMBNAIL = "poster_path";
    private static final String JSON_NULL_RESULT = "Unknown";

    /**
     * Create a private constructor because no one should ever create a {@link NetworkUtils} object.
     * This class is only meant to hold static methods for networking.
     * <p>
     * Instancing this class will throw and exception.
     */
    private NetworkUtils() {
        throw new AssertionError();
    }

    /**
     * Method to create and validate a query URL
     *
     * @param queryType: Either top rated or most popular
     */
    public static URL createUrl(String queryType) {
        // Create an URL Object and assign null value to it
        URL url = null;
        // Concatenate URL depending on which sort is being used:
        // Popular or Top Rated
        String queryUrl = BASE_URL_QUERY + queryType + STUDENT_API_KEY;
        try {
            // Form the query URL
            url = new URL(queryUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        // Return the formed URL or null value if invalid
        return url;
    }

    /**
     * Static method that makes the HTTP connection type GET to proceed with the query
     *
     * @param url: Url passed as a parameter from the {@link MovieLoader} class,
     *             already validated by the createUrl method of this class
     */

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // Check if urk is null before open connection
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            // Open connection
            urlConnection = (HttpURLConnection) url.openConnection();
            // Define HTTP request method to GET
            urlConnection.setRequestMethod("GET");
            // Define read timeout to 10 seconds
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            // Define connection timeout to 15 seconds
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            // CONNECT! :)
            urlConnection.connect();
            Log.i("HTTP Response Code:", String.valueOf(urlConnection.getResponseCode()));
            // Check if response code is 200 (OK) to proceed parsing data
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, String.valueOf(urlConnection.getResponseCode()));
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception thrown:" + e.toString());
        } finally {
            // Disconnect if connection is null
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            // Close connection if stream is null
            if (inputStream != null) {
                inputStream.close();
            }
        }
        // Return response to be parsed as a JSON later on
        return jsonResponse;
    }

    /**
     * Read the stream of bytes received and transforms into a String to be used later by JSON
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        // Create an output as a StringBuilder
        StringBuilder output = new StringBuilder();
        // If stream received as a parameter is not null, proceed with reading
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        // Return output as a String
        return output.toString();
    }

    public static List<Movie> extractMoviesFromJson(String parsedJson) {
        // Check if JSON passed as parameter is null
        if (TextUtils.isEmpty(parsedJson)) {
            // Return a null Movie list if parsedJson is null/empty
            return null;
        }
        // Initialise ArrayList of Movie objects
        ArrayList<Movie> movies = new ArrayList<>();

        // Try to create a movie list from the parsed JSON
        try {
            JSONObject baseJsonResponse = new JSONObject(parsedJson);
            JSONArray itemsArray = baseJsonResponse.getJSONArray(JSON_RESULTS);
            // Loops through the itemsArray
            for (int i = 0; i < itemsArray.length(); i++) {
                // Select JSON item according to position i in the array
                JSONObject item = itemsArray.getJSONObject(i);
                // Pass movie title to a String using parseMovieInformation method
                String title = parseMovieInformation(item, JSON_TITLE);
                // Pass movie synopsis to a String using parseMovieInformation method
                String synopsis = parseMovieInformation(item, JSON_SYNOPSIS);
                // Pass movie rating to a String using parseMovieInformation method
                String rating = parseMovieInformation(item, JSON_RATING);
                // Pass movie release date to a String using parseMovieInformation method
                String releaseDate = parseMovieInformation(item, JSON_RELEASE_DATE);
                // Pass movie thumbnail Url to a String using parseMovieInformation method
                String thumbnail = BASE_URL_THUMBNAIL + parseMovieInformation(item, JSON_THUMBNAIL);

                // Create new Movie object
                Movie movie = new Movie(title, synopsis, rating, releaseDate, thumbnail);
                // Add new Movie to the ArrayList of movies
                movies.add(movie);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Exception:" + e.toString());

        }
        // Return ArrayList of movies; or null if nothing is retrieved or there's an error
        return movies;
    }

    /**
     * Method used to parse simple Strings from {@link JSONObject}
     *
     * @param jsonObject: Declare the {@link JSONObject} being parsed
     * @param jsonTag:    Declare the desired JSON tag according to the API - in this case - The Movie DB
     */
    private static String parseMovieInformation(JSONObject jsonObject, String jsonTag) {
        try {
            // Try to return a String value with jsonTag being parsed from jsonObject
            return jsonObject.getString(jsonTag);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing book information from The Movie DB API", e);
        }
        // Return a constant String if not successfully parsed from jsonObject
        return JSON_NULL_RESULT;
    }

}
