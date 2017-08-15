package com.example.android.popflicks.utils;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.android.popflicks.Movie;
import com.example.android.popflicks.data.MovieContract;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.android.popflicks.MainActivity.LOG_TAG;

/**
 * Pop Flicks
 * Created by Mauricio on August 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 2
 */
public class Utils {

    /**
     * Convert from drawable to byte array
     *
     * @param drawable: Data retrieved from the movie poster web query that will be
     *              converted to byte[] in order to store in database BLOB
     */
    public static byte[] getBytes(Drawable drawable) {
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /** Create a Movie list from a Cursor object */
    public static List<Movie> extractFromCursor(Cursor cursor) {
        // Move cursor to first position
        cursor.moveToFirst();
        // Create list to store result
        List<Movie> data = new ArrayList<>();
        // Loop through cursor and extract data to pass to List
        for (int i= 0; i < cursor.getCount(); i++) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MDB_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_TITLE));
            String synopsis = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_SYNOPSIS));
            String rating = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_RATING));
            String releaseDate = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            byte[] imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_IMAGE_BLOB));
            Movie movie = new Movie(id, title, synopsis, rating, releaseDate, null, imageBlob);
            data.add(movie);
            cursor.moveToNext();
        }
        return data;
    }

    /** Convert from yyyy-MM-dd to yyyy only */
    public static String convertDateToYear(String inputDate) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("yyyy");
        Date date = new Date();
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Parsing Exception thrown:" + e.toString());
        }
        return outputFormat.format(date);
    }

}
