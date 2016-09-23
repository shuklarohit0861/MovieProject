package com.shukla.rohit.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.TextView;

import com.shukla.rohit.movies.Model.GetResposeInterface;
import com.shukla.rohit.movies.Model.MovieReviews;
import com.shukla.rohit.movies.Model.ReviewDetails;
import com.shukla.rohit.movies.Model.TheMovieDataBase;
import com.shukla.rohit.movies.Model.VideoTrailer;
import com.shukla.rohit.movies.Model.VideoTralerDetails;
import com.shukla.rohit.movies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment  {

    String[] projection = new String[]{
            MovieContract.Movie._ID,
            MovieContract.Movie.COLUMN_ID,
            MovieContract.Movie.COLUMN_TITLE,
            MovieContract.Movie.COLUMN_POSTER_PATH,
            MovieContract.Movie.COLUMN_VOTE_AVERAGE,
            MovieContract.Movie.COLUMN_RELEASE_DATE,
            MovieContract.Movie.COLUMN_OVERVIEW
    };
     private YoutubeAdapter youtubeAdapter;
     private TextView releaseDate ;
    private ImageView poster;
    private TextView rating;
    private TextView title;
    private TextView overviewTextView;
    String url = "http://image.tmdb.org/t/p/w500/";
    private static final int MOVIE_DETAIL_LOADER = 0;
    private static final int YOUTUBE_LOADER = 1;
    Call<VideoTrailer> call;
    Call<MovieReviews> reviewsCall;
    private String APIKEY = "23133a089180c7fe6697cea84789f691";
    String id;
    private ListView mYoutubeListView;
    private static final int YOUTUBE_ADAPTER= 1;


    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        GetResposeInterface getResposeInterface = TheMovieDataBase.getClient().create(GetResposeInterface.class);
        Intent intent = getActivity().getIntent();
        Uri uri = intent.getData();
        id = MovieContract.Movie.getMovieID(uri);

        if(count(id)== 0);
        {
            if(checkInternet(getActivity()))
            {
                call = getResposeInterface.getMovieTrailer(id,APIKEY);
                call.enqueue(new Callback<VideoTrailer>() {
                    @Override
                    public void onResponse(Call<VideoTrailer> call, Response<VideoTrailer> response) {

                        List<VideoTralerDetails> videoTrailerList = response.body().results;

                        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

                        for (VideoTralerDetails videoTralerDetails :videoTrailerList)
                        {
                            ContentValues values = new ContentValues();
                            values.put(MovieContract.Youtube.ID,id);
                            values.put(MovieContract.Youtube.YOUTUBE_ID,videoTralerDetails.id);
                            contentValuesList.add(values);
                        }

                        ContentValues[] youtube = new ContentValues[contentValuesList.size()];
                        contentValuesList.toArray(youtube);
                        getActivity().getContentResolver().bulkInsert(MovieContract.Youtube.CONTENT_URI,youtube);
                    }

                    @Override
                    public void onFailure(Call<VideoTrailer> call, Throwable t) {

                    }
                });

                reviewsCall = getResposeInterface.getMovieReview(id,APIKEY);
                reviewsCall.enqueue(new Callback<MovieReviews>() {
                    @Override
                    public void onResponse(Call<MovieReviews> call, Response<MovieReviews> response) {

                        List<ReviewDetails> reviewDetailsList = response.body().results;
                        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
                        for(ReviewDetails details:reviewDetailsList)
                        {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MovieContract.MovieReview.ID,id);
                            contentValues.put(MovieContract.MovieReview.REVIEW,details.content);
                            contentValues.put(MovieContract.MovieReview.REVIEWED_BY,details.author);
                            contentValuesList.add(contentValues);
                        }

                        ContentValues [] reviews = new ContentValues[contentValuesList.size()];
                        contentValuesList.toArray(reviews);
                        getActivity().getContentResolver().bulkInsert(MovieContract.MovieReview.CONTENT_URI,reviews);
                    }

                    @Override
                    public void onFailure(Call<MovieReviews> call, Throwable t) {

                    }
                });
            }
        }
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        mYoutubeListView = (ListView) view.findViewById(R.id.listView);
        releaseDate = (TextView)view.findViewById(R.id.releaseDateTextView);
        rating = (TextView) view.findViewById(R.id.ratingTextView);
        title = (TextView) view.findViewById(R.id.title_TextView);
        poster = (ImageView) view.findViewById(R.id.poster);
        overviewTextView = (TextView) view.findViewById(R.id.overviewTextView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER,null,new DetailsLoader());
        getLoaderManager().initLoader(YOUTUBE_LOADER,null,new LoaderYouTube());
        super.onActivityCreated(savedInstanceState);

    }

    private class DetailsLoader implements LoaderManager.LoaderCallbacks<Cursor> {

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
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.moveToFirst()) {
                title.setText(data.getString(2));
                Log.v("date", data.getString(5));
                releaseDate.setText(data.getString(5));
                rating.setText(data.getString(4));
                Picasso.with(getActivity()).load(url + data.getString(3)).into(poster);
                overviewTextView.setText(data.getString(6));

            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

    }

    private class LoaderYouTube implements LoaderManager.LoaderCallbacks<Cursor>
    {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new CursorLoader(getContext(), MovieContract.Youtube.buildYoutubeId(String.valueOf(id)),
                    new String[]{MovieContract.Youtube._ID,MovieContract.Youtube.ID, MovieContract.Youtube.YOUTUBE_ID},
                    null,
                    null,
                    null
                    );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            youtubeAdapter = new YoutubeAdapter(getContext(),data,YOUTUBE_ADAPTER);
            mYoutubeListView.setAdapter(youtubeAdapter);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

            youtubeAdapter.swapCursor(null);
        }
    }

    private int count(String id)
    {
            Uri uri = MovieContract.Youtube.buildYoutubeId(id);
        Log.v("Youtube URI",String.valueOf(uri));
        Cursor count = getActivity().getContentResolver().query(MovieContract.Youtube.buildYoutubeId(id),
                        new String[]{MovieContract.Youtube.ID},
                        null,
                        null,
                        null);
                return count.getCount();

            }
    public boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
