package com.example.android.popflicks.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Pop Flicks
 * Created by Mauricio on August 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 2
 */
public class MovieContract {

    /** Define Content Authority according to {@link android.Manifest} and {@link MovieProvider} */
    public static final String CONTENT_AUTHORITY = "com.example.android.popflicks";

    /** Define Content URI  */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**  Define path for the table which will be appended to the base content URI */
    public static final String PATH_MOVIES = "movies";

    private MovieContract() {
    }

    public static final class MovieEntry implements BaseColumns {

        // Create a full URI for the class
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        // Table name
        public static final String TABLE_NAME = "movies";

        // The _id field to index the table content
        public static final String _ID = BaseColumns._ID;

        // ID from the movie db
        public static final String COLUMN_MDB_ID = "mdb_id";

        // Original movie title
        public static final String COLUMN_TITLE = "title";

        // Plot synopsis
        public static final String COLUMN_SYNOPSIS = "synopsis";

        // User rating from the community
        public static final String COLUMN_RATING = "rating";

        // Movie release date
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // Poster image thumbnail
        public static final String COLUMN_IMAGE_BLOB = "image";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of movies.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single movie.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
    }

}
