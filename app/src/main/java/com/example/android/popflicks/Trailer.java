package com.example.android.popflicks;

/**
 * Pop Flicks
 * Created by Mauricio on August 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 2
 */
public class Trailer {

    /** Member variables to instantiate this object that will populate the UI
     * on de {@link DetailActivity} with trailers */
    private String mKey;
    private String mName;
    /** Constant to define YouTube's path to watch the trailer - used with an intent in {@link DetailActivity }*/
    private static final String YOUTUBE_PATH = "https://www.youtube.com/watch?v=";

    public Trailer(String mKey, String mName) {
        this.mKey = mKey;
        this.mName = mName;
    }

    /** This getter returns the URL which is the combination of the YOUTUBE_PATH
     *  and the video key parsed from the JSON */
    public String getTrailerUrl() {
        return YOUTUBE_PATH + mKey;
    }

    public String getmName() {
        return mName;
    }
}
