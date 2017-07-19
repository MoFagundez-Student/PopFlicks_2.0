package com.example.android.popflicks;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Pop Flicks
 * Created by Mauricio on July 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 1
 */

/** Since we are creating a favourite movies app, we are going to create this class
 * {@link Movie} and instantiate for every movie title we retrieve from the API.
 *
 * The class also implements {@link Parcelable} to facilitate moving Movie objects
 * between activities.
 * */
public class Movie implements Parcelable {

    /** Member variables */
    private String mTitle;          // Original movie title
    private String mSynopsis;       // Plot synopsis
    private String mRating;         // User rating from the community
    private String mReleaseDate;    // Movie release date
    private String mThumbnail;      // Poster image thumbnail

    /** Constants */
    private static final int PARCELABLE_SIZE = 5;
    private static final int PARCELABLE_POSITION_TITLE = 0;
    private static final int PARCELABLE_POSITION_SYNOPSIS = 1;
    private static final int PARCELABLE_POSITION_RATING = 2;
    private static final int PARCELABLE_POSITION_RELEASE_DATE = 3;
    private static final int PARCELABLE_POSITION_THUMBNAIL = 4;
    private static final int ZERO_CONTENT = 0;

    /** Class default constructor */
    public Movie(String mTitle, String mSynopsis, String mRating, String mReleaseDate, String mThumbnail) {
        this.mTitle = mTitle;
        this.mSynopsis = mSynopsis;
        this.mRating = mRating;
        this.mReleaseDate = mReleaseDate;
        this.mThumbnail = mThumbnail;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /** Class getters */
    public String getTitle() {
        return mTitle;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public String getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    @Override
    public int describeContents() {
        return ZERO_CONTENT;
    }

    /**
     * Method used to "deflate" the {@link Movie} before sending to {@link DetailActivity}
     * */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]
                {
                        this.mTitle,
                        this.mSynopsis,
                        this.mRating,
                        this.mReleaseDate,
                        this.mThumbnail
                });
    }

    /**
     * Method used to "inflate" the {@link Movie} once it has reached {@link DetailActivity}
     * */
    public Movie(Parcel in)
    {
        String[] data = new String[PARCELABLE_SIZE];
        in.readStringArray(data);
        this.mTitle = data[PARCELABLE_POSITION_TITLE];
        this.mSynopsis = data[PARCELABLE_POSITION_SYNOPSIS];
        this.mRating = data[PARCELABLE_POSITION_RATING];
        this.mReleaseDate = data[PARCELABLE_POSITION_RELEASE_DATE];
        this.mThumbnail = data[PARCELABLE_POSITION_THUMBNAIL];
    }

}
