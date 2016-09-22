package com.shukla.rohit.movies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.shukla.rohit.movies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by thero on 16-09-2016.
 */
public class MovieAdapter extends CursorAdapter {
    public MovieAdapter(Context context, Cursor c, int flag) {
        super(context, c, flag);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.img,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    public static class ViewHolder
    {
        public final ImageView imageView;
        public ViewHolder(View view)
        {
            imageView = (ImageView) view.findViewById(R.id.imageViewRecycle);
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
         int posterPathIndex = cursor.getColumnIndex(MovieContract.Movie.COLUMN_POSTER_PATH);
        String posterPath = cursor.getString(posterPathIndex);
        String url = "http://image.tmdb.org/t/p/w500/"+ posterPath;
        Picasso.with(context).load(url).into(viewHolder.imageView);
    }
}
