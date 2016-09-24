package com.shukla.rohit.movies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.shukla.rohit.movies.data.MovieContract;

/**
 * Created by thero on 24-09-2016.
 */
public class ReviewAdapter extends CursorAdapter {
    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.review,viewGroup,false);
        ViewHolderReview viewHolderReview = new ViewHolderReview(view);
        view.setTag(viewHolderReview);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolderReview viewHolderReview = (ViewHolderReview) view.getTag();
        int review = cursor.getColumnIndex(MovieContract.MovieReview.REVIEW);
        int reviewer = cursor.getColumnIndex(MovieContract.MovieReview.REVIEWED_BY);
        String reviewText = cursor.getString(review);
        String reviewerName = cursor.getString(reviewer);
        viewHolderReview.reviewer.setText(reviewerName);
        viewHolderReview.review.setText(reviewText);
    }

    private static class ViewHolderReview
    {
        private final TextView review;
        private final TextView reviewer;
        public ViewHolderReview(View view)
        {
            review = (TextView) view.findViewById(R.id.review);
            reviewer = (TextView) view.findViewById(R.id.reviewer);

        }



    }
}
