package com.example.android.popflicks;

/**
 * Pop Flicks
 * Created by Mauricio on August 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 2
 */
public class Review {

    /** Member variables for the class that will be instantiated to
     * populate the UI on the {@link com.example.android.popflicks.DetailActivity} */
    private String mAuthor;
    private String mContent;

    public Review(String mAuthor, String mContent) {
        this.mAuthor = mAuthor;
        this.mContent = mContent;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmContent() {
        return mContent;
    }

}
