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
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

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
            MovieContract.Movie.COLUMN_OVERVIEW,
            MovieContract.Movie.COLUMN_FAVORITE_MOVIES
    };
    private YoutubeAdapter youtubeAdapter;
    private ReviewAdapter reviewAdapter;
    private TextView releaseDate ;
    private ImageView poster;
    private TextView rating;
    private TextView title;
    boolean fav;
    private TextView overviewTextView;
    String url = "http://image.tmdb.org/t/p/w500/";
    private static final int MOVIE_DETAIL_LOADER = 0;
    private static final int YOUTUBE_LOADER = 1;
    private static final int REVIEW_LOADER = 2;
    Call<VideoTrailer> call;
    Call<MovieReviews> reviewsCall;
    private String APIKEY = "23133a089180c7fe6697cea84789f691";
    String movieId;
    private ExpandableHeightListView mYoutubeListView;
    private ExpandableHeightListView mReviewListview;
    private static final int YOUTUBE_ADAPTER= 1;
    private static final int REVIEW_ADAPTER = 2;
    private ToggleButton toggleButton;


    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        GetResposeInterface getResposeInterface = TheMovieDataBase.getClient().create(GetResposeInterface.class);
        Intent intent = getActivity().getIntent();
        if(intent != null) {
            Uri uri = intent.getData();
            movieId = MovieContract.Movie.getMovieID(uri);

            Cursor count = getActivity().getContentResolver().query(MovieContract.Youtube.buildYoutubeId(movieId),
                    new String[]{MovieContract.Youtube.ID},
                    null,
                    null,
                    null);

            Log.v("count",String.valueOf(count.getCount()));

            if(count.getCount() ==0)
            {
                if(checkInternet(getActivity()))
                {
                    call = getResposeInterface.getMovieTrailer(movieId,APIKEY);
                    reviewsCall = getResposeInterface.getMovieReview(movieId,APIKEY);
                    if(!call.isExecuted()&&!reviewsCall.isExecuted()) {
                        call.enqueue(new Callback<VideoTrailer>() {
                            @Override
                            public void onResponse(Call<VideoTrailer> call, Response<VideoTrailer> response) {

                                List<VideoTralerDetails> videoTrailerList = response.body().results;

                                List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

                                for (VideoTralerDetails videoTralerDetails : videoTrailerList) {
                                    ContentValues values = new ContentValues();
                                    values.put(MovieContract.Youtube.ID, movieId);
                                    values.put(MovieContract.Youtube.YOUTUBE_ID, videoTralerDetails.key);
                                    contentValuesList.add(values);
                                }

                                ContentValues[] youtube = new ContentValues[contentValuesList.size()];
                                contentValuesList.toArray(youtube);
                                getActivity().getContentResolver().bulkInsert(MovieContract.Youtube.CONTENT_URI, youtube);

                            }

                            @Override
                            public void onFailure(Call<VideoTrailer> call, Throwable t) {

                            }
                        });


                        reviewsCall.enqueue(new Callback<MovieReviews>() {
                            @Override
                            public void onResponse(Call<MovieReviews> call, Response<MovieReviews> response) {

                                List<ReviewDetails> reviewDetailsList = response.body().results;
                                List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
                                for (ReviewDetails details : reviewDetailsList) {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(MovieContract.MovieReview.ID, movieId);
                                    contentValues.put(MovieContract.MovieReview.REVIEW, details.content);
                                    contentValues.put(MovieContract.MovieReview.REVIEWED_BY, details.author);
                                    contentValuesList.add(contentValues);
                                }

                                ContentValues[] reviews = new ContentValues[contentValuesList.size()];
                                contentValuesList.toArray(reviews);
                                getActivity().getContentResolver().bulkInsert(MovieContract.MovieReview.CONTENT_URI, reviews);
                            }

                            @Override
                            public void onFailure(Call<MovieReviews> call, Throwable t) {

                            }
                        });
                    }
                }
            }

        }


        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        getLoaderManager().initLoader(YOUTUBE_LOADER,null,new LoaderYouTube());
        getLoaderManager().initLoader(REVIEW_LOADER,null,new ReviewLoader());
        mYoutubeListView = (ExpandableHeightListView) view.findViewById(R.id.listView);
        mYoutubeListView.setExpanded(true);
        mYoutubeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                int index = cursor.getColumnIndex(MovieContract.Youtube.YOUTUBE_ID);
                String key = cursor.getString(index);
                String url = "https://www.youtube.com/watch?v="+key;
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse(url));
                startActivity(intent1);
            }
        });
        toggleButton = (ToggleButton) view.findViewById(R.id.likebutton);
       toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                   ContentValues contentValues = new ContentValues();
                   contentValues.put(MovieContract.Movie.COLUMN_FAVORITE_MOVIES,!fav);
                   getActivity().getContentResolver().update(MovieContract.Movie.buildMovieID(movieId),
                           contentValues,
                           MovieContract.Movie.COLUMN_ID +" = ?",
                           new String[]{movieId}
                           );

           }
       });
        mReviewListview = (ExpandableHeightListView) view.findViewById(R.id.listView_review);
        mReviewListview.setExpanded(true);
        releaseDate = (TextView)view.findViewById(R.id.releaseDateTextView);
        rating = (TextView) view.findViewById(R.id.ratingTextView);
        title = (TextView) view.findViewById(R.id.title_TextView);
        poster = (ImageView) view.findViewById(R.id.poster);
        overviewTextView = (TextView) view.findViewById(R.id.overviewTextView);
        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER,null,new DetailsLoader());
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
                fav = Boolean.getBoolean(data.getString(7));
                toggleButton.setChecked(fav);

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

            return new CursorLoader(getContext(), MovieContract.Youtube.buildYoutubeId(String.valueOf(movieId)),
                    new String[]{MovieContract.Youtube._ID, MovieContract.Youtube.YOUTUBE_ID},
                    null,
                    null,
                    null
                    );
        }
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v("data",String.valueOf(data.getCount()));
            youtubeAdapter = new YoutubeAdapter(getContext(),data,YOUTUBE_ADAPTER);
            mYoutubeListView.setAdapter(youtubeAdapter);
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            youtubeAdapter.swapCursor(null);
        }
    }

    private class ReviewLoader implements LoaderManager.LoaderCallbacks<Cursor>
    {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getContext(), MovieContract.MovieReview.buildMovieReviewId(movieId),
                    new String[]{MovieContract.MovieReview._ID, MovieContract.MovieReview.REVIEW, MovieContract.MovieReview.REVIEWED_BY},
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                reviewAdapter = new ReviewAdapter(getContext(),data,REVIEW_LOADER);
            mReviewListview.setAdapter(reviewAdapter);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            reviewAdapter.swapCursor(null);


        }
    }


    public boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
