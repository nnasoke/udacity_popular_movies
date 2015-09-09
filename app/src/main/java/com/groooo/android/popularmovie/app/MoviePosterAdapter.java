package com.groooo.android.popularmovie.app;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.groooo.android.popularmovie.R;
import com.groooo.android.popularmovie.helper.MovieApi;
import com.squareup.picasso.Picasso;

public class MoviePosterAdapter extends CursorAdapter {

    public MoviePosterAdapter(Context context, Cursor c, int flag) {
        super(context, c, flag);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_movies_grid_item, viewGroup, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String imgPath = cursor.getString(cursor.getColumnIndex("poster_path"));
        final Uri imgUri = MovieApi.getPosterImageUri(imgPath);
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        Picasso.with(context).load(imgUri).into(viewHolder.imageView);
    }

    // View holder class.
    static class ViewHolder {
        public ImageView imageView;

        public ViewHolder(View root) {
            this.imageView = (ImageView)root.findViewById(R.id.img_movie_poster);
        }
    }
}
