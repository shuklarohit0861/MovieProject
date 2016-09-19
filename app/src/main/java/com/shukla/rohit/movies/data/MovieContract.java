package com.shukla.rohit.movies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by thero on 05-09-2016.
 */
public class MovieContract {

    public static final String AUTHORITY = "com.shukla.rohit.movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "Movies";

    public static final String PATH_YOUTUBE = "YouTube";

    public static final String PATH_MOVIE_REVIEW = "MovieReviews";




    public static final class Movie implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+AUTHORITY+"/"+ PATH_MOVIES;

        public static final String CONTENT_TYPE_ITME = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"+AUTHORITY + "/"+ PATH_MOVIES;


        public static final String TABLE_NAME ="Movies";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_OVERVIEW = "OverView";
        public static final String COLUMN_RELEASE_DATE = "release_data";
        public static final String COLUMN_VOTE_AVERAGE = "Vote_Average";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULAR_MOVIES = "popular_movies";
        public static final String COLUMN_TOPRATED_MOVIES = "toprated_movies";
        public static final String COLUMN_FAVORITE_MOVIES = "favorite_movies";
        public static final String COLUMN_POPULARITY = "popularity";

        public static Uri buildMovieURI (long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static final Uri buildMovieID (String id)
        {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static final String getMovieID(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class Youtube implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_YOUTUBE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+AUTHORITY+"/"+PATH_YOUTUBE;

        public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +AUTHORITY +"/"+PATH_YOUTUBE;

        public static final String TABLE_NAME = "YouTube";
        public static final String ID = "Id";
        public static final String YOUTUBE_ID = "youtube_id";

        public static Uri buildYouTube (Long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static final Uri buildYoutubeId(String id)
        {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static final String getYoutubeId (Uri uri)
        {
            return uri.getPathSegments().get(1);
        }



    }

    public static final class MovieReview implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_REVIEW).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/" + AUTHORITY +"/"+ PATH_MOVIE_REVIEW;

        public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/"+ PATH_MOVIE_REVIEW;

        public static final String TABLE_NAME ="MovieReviews";
        public static final String ID = "id";
        public static final String REVIEW = "review";
        public static final String REVIEWED_BY = "reviewed_by";

        public static Uri bulidMovieReview (Long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static final Uri buildMovieReviewId(String id)
        {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static final String getMovieReviewId(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }
    }


}
