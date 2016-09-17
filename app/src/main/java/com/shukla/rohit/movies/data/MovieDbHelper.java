package com.shukla.rohit.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by thero on 06-09-2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";

    static final int DATABASEVERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE_MOVIE = " CREATE TABLE " + MovieContract.Movie.TABLE_NAME +
                " (" + MovieContract.Movie.COLUMN_ID + " TEXT PRIMARY KEY," +
                MovieContract.Movie.COLUMN_TITLE +" TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MovieContract.Movie.COLUMN_RELEASE_DATE + " DATE NOT NULL," +
                MovieContract.Movie.COLUMN_VOTE_COUNT + " REAL NOT NULL," +
                MovieContract.Movie.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                MovieContract.Movie.COLUMN_POPULAR_MOVIES + " BOOLEAN DEFAULT 'FALSE',"+
                MovieContract.Movie.COLUMN_TOPRATED_MOVIES + " BOOLEAN DEFAULT 'FALSE'," +
                MovieContract.Movie.COLUMN_FAVORITE_MOVIES + " BOOLEAN DEFAULT 'FALSE', "+
                MovieContract.Movie.COLUMN_POPULARITY + " REAL NOT NULL " +
                " );";
        Log.v("Movie TABLE",CREATE_TABLE_MOVIE);

        final String CREATE_TABLE_YOUTUBE = "CREATE TABLE " + MovieContract.Youtube.TABLE_NAME +
                " (" + MovieContract.Youtube.ID + " TEXT, " + MovieContract.Youtube.YOUTUBE_ID + " TEXT NOT NULL," +
                " FOREIGN KEY (" + MovieContract.Youtube.ID + ")"+ " REFERENCES " + MovieContract.Movie.TABLE_NAME + " ("
                + MovieContract.Movie.COLUMN_ID + ") ON DELETE CASCADE );";
        Log.v("YouTUBE TABLE", CREATE_TABLE_YOUTUBE);

        final String CREATE_TABLE_MOVIE_REVIEW = "CREATE TABLE " + MovieContract.MovieReview.TABLE_NAME +
                " (" + MovieContract.MovieReview.ID + " TEXT, " + MovieContract.MovieReview.REVIEW + " TEXT NOT NULL,"+
                MovieContract.MovieReview.REVIEWED_BY + " TEXT NOT NULL," + "FOREIGN KEY (" + MovieContract.MovieReview.ID +") "+
                "REFERENCES " + MovieContract.Movie.TABLE_NAME +" ("+ MovieContract.Movie.COLUMN_ID + ") ON DELETE CASCADE );";

        Log.v("MOVIE REVIEW ", CREATE_TABLE_MOVIE_REVIEW);

            sqLiteDatabase.execSQL(CREATE_TABLE_MOVIE);
            sqLiteDatabase.execSQL(CREATE_TABLE_YOUTUBE);
            sqLiteDatabase.execSQL(CREATE_TABLE_MOVIE_REVIEW);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.Movie.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieReview.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.Youtube.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
