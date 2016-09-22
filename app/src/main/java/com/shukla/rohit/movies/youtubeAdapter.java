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
 * Created by thero on 21-09-2016.
 */
public class YoutubeAdapter extends CursorAdapter {
    public YoutubeAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.youtube,viewGroup,false);
        YoutubeAdapter.ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int posterPathIndex = cursor.getColumnIndex(MovieContract.Youtube.YOUTUBE_ID);
        String youtubeId = cursor.getString(posterPathIndex);
        String url = "https://img.youtube.com/vi/"+youtubeId+"/0.jpg";
        Picasso.with(context).load(url).into(viewHolder.imageView);

    }
    public static class ViewHolder
    {
        public final ImageView imageView;

        public ViewHolder(View view)

        {
            imageView = (ImageView) view.findViewById(R.id.youtubeListImg);
        }
    }

}
