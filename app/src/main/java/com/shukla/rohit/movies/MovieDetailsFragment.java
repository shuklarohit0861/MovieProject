package com.shukla.rohit.movies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shukla.rohit.movies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    String[] projection = new String[]{
            MovieContract.Movie._ID,
            MovieContract.Movie.COLUMN_ID,
            MovieContract.Movie.COLUMN_TITLE,
            MovieContract.Movie.COLUMN_POSTER_PATH,
            MovieContract.Movie.COLUMN_VOTE_AVERAGE,
            MovieContract.Movie.COLUMN_RELEASE_DATE,
            MovieContract.Movie.COLUMN_OVERVIEW
    };
     private TextView releaseDate ;
    private ImageView poster;
    private TextView rating;
    private TextView title;
    String url = "http://image.tmdb.org/t/p/w500/";
    private static final int MOVIE_DETAIL_LOADER = 0;


    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        releaseDate = (TextView)view.findViewById(R.id.ratingTextView);
        rating = (TextView) view.findViewById(R.id.ratingTextView);
        title = (TextView) view.findViewById(R.id.title_TextView);
        poster = (ImageView) view.findViewById(R.id.poster);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = getActivity().getIntent();
        if (intent == null || intent.getData() == null) {
            return null;
        }
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null && data.moveToFirst())
        {
            title.setText(data.getString(2));
            Log.v("Title",data.getString(2));
            releaseDate.setText(data.getString(5));
            rating.setText(data.getString(4));
            Picasso.with(getActivity()).load(url+data.getString(3));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
