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
    private String mId;             // ID from the movie db
    private String mTitle;          // Original movie title
    private String mSynopsis;       // Plot synopsis
    private String mRating;         // User rating from the community
    private String mReleaseDate;    // Movie release date
    private String mThumbnail;      // Poster image thumbnail
    private byte[] mImageBlob;      // Poster image when stored/retrieved from the database

    /** Constants */
    private static final int PARCELABLE_SIZE = 6;
    private static final int PARCELABLE_POSITION_TITLE = 0;
    private static final int PARCELABLE_POSITION_SYNOPSIS = 1;
    private static final int PARCELABLE_POSITION_RATING = 2;
    private static final int PARCELABLE_POSITION_RELEASE_DATE = 3;
    private static final int PARCELABLE_POSITION_THUMBNAIL = 4;
    private static final int PARCELABLE_POSITION_ID = 5;
    private static final int ZERO_CONTENT = 0;


    /** Class constructor */
    public Movie(String mId, String mTitle, String mSynopsis, String mRating, String mReleaseDate, String mThumbnail, byte[] mImageBlob) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mSynopsis = mSynopsis;
        this.mRating = mRating;
        this.mReleaseDate = mReleaseDate;
        this.mThumbnail = mThumbnail;
        this.mImageBlob = mImageBlob;
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

    /** Single class setter to pass the image byte array into the object
     * in order to inflate/deflate in other views with the objective of use less network
     * and also store in the database blob */
    public void setImageBlob(byte[] mImageBlob) {
        this.mImageBlob = mImageBlob;
    }

    /** Class getters */
    public String getId () {
        return mId;
    }

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

    public byte[] getImageBlob() {
        return mImageBlob;
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
                        this.mThumbnail,
                        this.mId
                });
        dest.writeInt(this.mImageBlob.length);
        dest.writeByteArray(this.getImageBlob());
    }

    /**
     * Method used to "inflate" the {@link Movie} once it has reached {@link DetailActivity}
     * */
    public Movie(Parcel in)
    {
        String[] data = new String[PARCELABLE_SIZE];
        in.readStringArray(data);
        this.mId = data[PARCELABLE_POSITION_ID];
        this.mTitle = data[PARCELABLE_POSITION_TITLE];
        this.mSynopsis = data[PARCELABLE_POSITION_SYNOPSIS];
        this.mRating = data[PARCELABLE_POSITION_RATING];
        this.mReleaseDate = data[PARCELABLE_POSITION_RELEASE_DATE];
        this.mThumbnail = data[PARCELABLE_POSITION_THUMBNAIL];
        this.mImageBlob = new byte[in.readInt()];
        in.readByteArray(mImageBlob);
    }

}
