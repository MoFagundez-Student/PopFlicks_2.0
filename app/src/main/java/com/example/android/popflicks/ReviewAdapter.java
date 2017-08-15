package com.example.android.popflicks;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Pop Flicks
 * Created by Mauricio on August 10, 2017
 * <p>
 * Udacity Android Developer Nanodegree
 * Project 1: Popular Movies - Stage 2
 */
public class ReviewAdapter extends ArrayAdapter<Review> {

    private TextView mAuthor;
    private TextView mContent;
    private ArrayList<Review> mReviews;
    private Context mContext;

    public ReviewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Review> data) {
        super(context, resource, data);
        this.mReviews = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Review review = mReviews.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (null == convertView) {
            // Here we are using findViewById in lieu of ButterKnife
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.review_list_item, null);
            mAuthor = (TextView) convertView.findViewById(R.id.text_view_review_author);
            mContent = (TextView) convertView.findViewById(R.id.text_view_review_content);
        }
        mAuthor.setText(review.getmAuthor());
        mContent.setText(review.getmContent());
        // Return view to be displayed to the user
        return convertView;
    }
}
