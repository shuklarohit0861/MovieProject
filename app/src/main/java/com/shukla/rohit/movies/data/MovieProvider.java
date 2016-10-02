package com.shukla.rohit.movies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by thero on 07-09-2016.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher ()
    {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.AUTHORITY;

        uriMatcher.addURI(authority,MovieContract.PATH_MOVIES,MOVIES);
        uriMatcher.addURI(authority,MovieContract.PATH_YOUTUBE,YOUTUBE);
        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE_REVIEW,REVIEW);
        uriMatcher.addURI(authority,MovieContract.PATH_MOVIES + "/#",MOVIES_WITH_ID);
        uriMatcher.addURI(authority,MovieContract.PATH_YOUTUBE+"/#",YOUTUBE_WITH_ID);
        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE_REVIEW+"/#",REVIEW_WITH_ID);

        return uriMatcher;
    }

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 200;
    public static final int YOUTUBE_WITH_ID = 300;
    public static final int REVIEW_WITH_ID = 400;
    public static final int YOUTUBE = 500;
    public static final int REVIEW = 600;

    private MovieDbHelper mDbHelper ;

    private static final SQLiteQueryBuilder mSQLiteQUeryBuilder = new SQLiteQueryBuilder();

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] column, String selection, String[] selectionArgu, String orderedBy) {

        Cursor cursor;
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case MOVIES: {
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.Movie.TABLE_NAME,
                        column,
                        selection,
                        selectionArgu,
                        null,
                        null,
                        orderedBy);
                break;
            }
            case MOVIES_WITH_ID:{
                String id = MovieContract.Movie.getMovieID(uri);
                selection = MovieContract.Movie.COLUMN_ID + " = ?";
                selectionArgu = new String[]{id};
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.Movie.TABLE_NAME,
                        column,
                        selection,
                        selectionArgu,
                        null,
                        null,
                        orderedBy
                );
                break;
            }
            case YOUTUBE_WITH_ID:
            {
                String id = MovieContract.Youtube.getYoutubeId(uri);
                selection = MovieContract.Youtube.ID + " = ? ";
                selectionArgu = new String[]{id};
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.Youtube.TABLE_NAME,
                        column,
                        selection,
                        selectionArgu,
                        null,
                        null,
                        orderedBy
                );
                break;
            }
            case REVIEW_WITH_ID:
            {
                String id = MovieContract.MovieReview.getMovieReviewId(uri);
                selection = MovieContract.MovieReview.ID + " = ?";
                selectionArgu = new String[]{id};
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.MovieReview.TABLE_NAME,
                        column,
                        selection,
                        selectionArgu,
                        null,
                        null,
                        orderedBy
                );
                break;
            }
            default: throw new UnsupportedOperationException("Unknown Uri :" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            case MOVIES :
                long id  = db.insert(MovieContract.Movie.TABLE_NAME,null,contentValues);
                if(id > 0) {
                    returnUri = MovieContract.Movie.buildMovieURI(id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
                default:
                    throw new UnsupportedOperationException("Unsupported operation "+uri);
        }

            getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgu) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int deleteRow;

        switch(match){
            case MOVIES:
            {
                deleteRow = db.delete(MovieContract.Movie.TABLE_NAME,selection,selectionArgu);
                break;
            }
            default:
                throw  new  UnsupportedOperationException("UnSupported Operation" + uri);
        }

        if (deleteRow != 0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return deleteRow;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updateRow;
        switch (match)
        {
            case MOVIES_WITH_ID:
            {
                String id = MovieContract.Movie.getMovieID(uri);
                selection = MovieContract.Movie.COLUMN_ID +" = ?";
                selectionArgs = new String[]{id};
                updateRow = db.update(MovieContract.Movie.TABLE_NAME,contentValues,selection,selectionArgs);
                break;
            }
            default:
                throw  new UnsupportedOperationException("Unsupported operation "+uri);

        }
        if (updateRow != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return updateRow;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String rawQuery = "SELECT COUNT(*) FROM " +MovieContract.Movie.TABLE_NAME + " WHERE " + MovieContract.Movie.COLUMN_ID + " = ?";

        Cursor cursor ;
        int returnCount= 0;

        switch (match){

            case MOVIES:
            {
                db.beginTransaction();

                try{
                    for (ContentValues values1 : values)
                    {
                        cursor = db.rawQuery(rawQuery,new String[] {values1.getAsString(MovieContract.Movie.COLUMN_ID)} );
                        cursor.moveToFirst();

                        if(cursor.getInt(0) != 0)
                        {

                             int i = db.update(MovieContract.Movie.TABLE_NAME,values1,MovieContract.Movie.COLUMN_ID + " = ?",
                                   new String[]{values1.getAsString(MovieContract.Movie.COLUMN_ID)});
                            returnCount = returnCount + i;
                        }
                        else
                        {
                            values1.put(MovieContract.Movie.COLUMN_FAVORITE_MOVIES,false);
                            long _id = db.insert(MovieContract.Movie.TABLE_NAME,null,values1);
                                if(_id != -1)
                                {
                                    returnCount++;
                                }
                        }

                    }

                    db.setTransactionSuccessful();

                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            }
            case YOUTUBE:
                db.beginTransaction();

                try{
                    for(ContentValues values1 : values)
                    {

                      long _id =  db.insert(MovieContract.Youtube.TABLE_NAME,null,values1);
                        if (_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;

            case REVIEW:
                db.beginTransaction();

                try{
                    for (ContentValues values1 : values)
                    {
                        long _id = db.insert(MovieContract.MovieReview.TABLE_NAME,null,values1);
                        if(_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);

        }
    }
}
