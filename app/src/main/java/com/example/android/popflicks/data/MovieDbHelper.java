package com.example.android.popflicks.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.popflicks.data.MovieContract.MovieEntry;

/**
 * Pop Flicks
 * Created by Mauricio on August 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 2
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "popflicks.db";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ( " +
            MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MovieEntry.COLUMN_MDB_ID + " INTEGER NOT NULL, " +
            MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_IMAGE_BLOB +  " BLOB NOT NULL ) " +
            ";";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("SQLENTRIES", SQL_CREATE_ENTRIES);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table or read from existent
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing for now :)
    }
}
