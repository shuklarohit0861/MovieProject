package com.shukla.rohit.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.shukla.rohit.movies.Model.GetResposeInterface;
import com.shukla.rohit.movies.Model.MovieModel;
import com.shukla.rohit.movies.Model.Result;
import com.shukla.rohit.movies.Model.TheMovieDataBase;
import com.shukla.rohit.movies.data.MovieContract;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String API_KEY = "";
    private static final int MOVIE_LOADER = 0;
    private MovieAdapter mMovieAdapter;
    private String mMoviePref;
     private SharedPreferences mPreferences;
    private String whereCondition;
    Call<MovieModel> call;
    boolean topMovies = false;
    boolean popularMovies = false;
     private String order = null;




    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        mMoviePref = mPreferences.getString(getString(R.string.key_pref),getString(R.string.popular_movies_value));


        mMovieAdapter = new MovieAdapter(getContext(),null,0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GetResposeInterface getResposeInterface = TheMovieDataBase.getClient().create(GetResposeInterface.class);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getContext(),MovieDetails.class)
                        .setData(MovieContract.Movie.buildMovieID(cursor.getString(1)));
                startActivity(intent);
            }
        });

        switch (mMoviePref)
        {
            case "0":
                whereCondition = MovieContract.Movie.COLUMN_FAVORITE_MOVIES + " = ?";
                ((MainActivity)getContext()).setTitle(getString(R.string.favorite));

                break;
            case "1":
                whereCondition = MovieContract.Movie.COLUMN_TOPRATED_MOVIES + " = ?";
                call = getResposeInterface.getTopRatedMovies(API_KEY);
                order = MovieContract.Movie.COLUMN_VOTE_AVERAGE + " DESC";
                topMovies = true;
                ((MainActivity)getContext()).setTitle(getString(R.string.toprated));
                break;
            case "2":
                whereCondition = MovieContract.Movie.COLUMN_POPULAR_MOVIES + " = ?";
                call = getResposeInterface.getPopularMovies(API_KEY);
                order = MovieContract.Movie.COLUMN_POPULARITY;
                popularMovies = true;
                ((MainActivity)getContext()).setTitle(getString(R.string.popular_movies));
                break;
        }

        Cursor check = getActivity().getContentResolver().query(MovieContract.Movie.CONTENT_URI,
                new String[]{MovieContract.Movie.COLUMN_ID},
                whereCondition,
                new String[]{"1"},
                null);

        if (check.getCount() == 0) {

            if(!checkInternet(getContext()))
            {
                Toast.makeText(getContext(), "check your internet connections....", Toast.LENGTH_LONG).show();
            }
            if  (mMoviePref.equals("0")) {
                Toast.makeText(getContext(), "you have no Favorite Movie", Toast.LENGTH_LONG).show();
            }
            else
            {
                call.enqueue(new Callback<MovieModel>() {
                    @Override
                    public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {

                        List<Result> results = response.body().results;
                        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

                        for (Result result : results) {
                            ContentValues values1 = new ContentValues();
                            values1.put(MovieContract.Movie.COLUMN_ID, result.id);
                            values1.put(MovieContract.Movie.COLUMN_TITLE, result.title);
                            values1.put(MovieContract.Movie.COLUMN_OVERVIEW, result.overview);
                            values1.put(MovieContract.Movie.COLUMN_RELEASE_DATE, result.releaseDate);
                            values1.put(MovieContract.Movie.COLUMN_VOTE_AVERAGE, result.voteAverage);
                            values1.put(MovieContract.Movie.COLUMN_POSTER_PATH, result.posterPath);
                            values1.put(MovieContract.Movie.COLUMN_TOPRATED_MOVIES, topMovies);
                            values1.put(MovieContract.Movie.COLUMN_POPULAR_MOVIES, popularMovies);
                            values1.put(MovieContract.Movie.COLUMN_POPULARITY, result.popularity);
                            contentValuesList.add(values1);
                        }

                        ContentValues[] moviedetails = new ContentValues[contentValuesList.size()];
                        contentValuesList.toArray(moviedetails);
                        getContext().getContentResolver().bulkInsert(MovieContract.Movie.CONTENT_URI, moviedetails);

                    }

                    @Override
                    public void onFailure(Call<MovieModel> call, Throwable t) {

                    }
                });

            }

        }

        gridView.setAdapter(mMovieAdapter);

        return rootView;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {



        Uri uri = MovieContract.Movie.CONTENT_URI;

        return new CursorLoader(getActivity(),
                uri,
                new String[]{MovieContract.Movie._ID, MovieContract.Movie.COLUMN_ID,MovieContract.Movie.COLUMN_POSTER_PATH},
                whereCondition,
                new String[]{"1"},
                order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);

    }
    public boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
