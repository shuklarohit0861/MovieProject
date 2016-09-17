package com.shukla.rohit.movies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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

    private static final String API_KEY = "23133a089180c7fe6697cea84789f691";
    private static final int MOVIE_LOADER = 0;
    private MovieAdapter mMovieAdapter;




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
        mMovieAdapter = new MovieAdapter(getContext(),null,0);




        Cursor check = getActivity().getContentResolver().query(MovieContract.Movie.CONTENT_URI,
                new String[]{MovieContract.Movie.COLUMN_ID},
                null,
                null,
                null);

        if (check.getCount() == 0)
        {
            GetResposeInterface getResposeInterface = TheMovieDataBase.getClient().create(GetResposeInterface.class);

            Call<MovieModel> call = getResposeInterface.getTopRatedMovies(API_KEY);

            call.enqueue(new Callback<MovieModel>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {

                    List<Result> results = response.body().results;
                    List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

                    for (Result result : results)
                    {
                        ContentValues values1 = new ContentValues();
                        values1.put(MovieContract.Movie.COLUMN_ID,result.id);
                        values1.put(MovieContract.Movie.COLUMN_TITLE,result.title);
                        values1.put(MovieContract.Movie.COLUMN_OVERVIEW,result.overview);
                        values1.put(MovieContract.Movie.COLUMN_RELEASE_DATE,result.releaseDate);
                        values1.put(MovieContract.Movie.COLUMN_VOTE_COUNT,result.voteCount);
                        values1.put(MovieContract.Movie.COLUMN_POSTER_PATH,result.posterPath);
                        values1.put(MovieContract.Movie.COLUMN_TOPRATED_MOVIES,true);
                        values1.put(MovieContract.Movie.COLUMN_POPULARITY,result.popularity);
                        contentValuesList.add(values1);
                    }

                    ContentValues[] moviedetails = new ContentValues[contentValuesList.size()];
                    contentValuesList.toArray(moviedetails);
                    getContext().getContentResolver().bulkInsert(MovieContract.Movie.CONTENT_URI,moviedetails);

                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {

                }
            });

        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);

        gridView.setAdapter(mMovieAdapter);

        return rootView;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String whereCondition = MovieContract.Movie.COLUMN_TOPRATED_MOVIES +" = ?";

        String order = MovieContract.Movie.COLUMN_POPULARITY + " ASC";
        Uri uri = MovieContract.Movie.CONTENT_URI;

        return new CursorLoader(getActivity(),
                uri,
                null,
                whereCondition,
                new String[]{"true"},
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
}
