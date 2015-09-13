package com.groooo.android.popularmovie.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.groooo.android.popularmovie.R;
import com.groooo.android.popularmovie.data.MovieContract;
import com.groooo.android.popularmovie.helper.MovieDbOrgHelper;
import com.groooo.android.popularmovie.helper.Utility;
import com.squareup.picasso.Picasso;

public class MovieDetailFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 9988;

    private static final String[] SELECT_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_ITEM_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH
    };

    private static final int CINDX_ITEM_ID = 0;
    private static final int CINDX_TITLE = 1;
    private static final int CINDX_RELEASE_DATE = 2;
    private static final int CINDX_VOTE_AVERAGE = 3;
    private static final int CINDX_OVERVIEW = 4;
    private static final int CINDX_POSTER_PATH = 5;
    private static final int CINDX_BACKDROP_PATH = 6;

    public MovieDetailFragment() { }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            return new CursorLoader(getActivity(), intent.getData(), SELECT_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!cursor.moveToFirst()) return;

        final String dateFormat = getString(R.string.basic_date_format);
        final String title = cursor.getString(CINDX_TITLE);
        final String overview = cursor.getString(CINDX_OVERVIEW);
        final long date = cursor.getLong(CINDX_RELEASE_DATE);
        final double rating = cursor.getDouble(CINDX_VOTE_AVERAGE);
        final Uri poster = MovieDbOrgHelper.getPosterImageUri(cursor.getString(CINDX_POSTER_PATH));

        ViewHolder viewHolder = (ViewHolder) getView().getTag();
        viewHolder.tvTitle.setText(Utility.textToSmoothDisplay(title));
        viewHolder.tvOverview.setText(Utility.textToSmoothDisplay(overview));
        viewHolder.tvReleaseDate.setText(Utility.timestampToBasicFormat(dateFormat, date));
        viewHolder.tvUserRating.setText(String.format("Rating: %s/10", Double.toString(rating)));
        Picasso.with(getActivity()).load(poster).into(viewHolder.ivPoster);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { /* Just .. do nothing */ }

    class ViewHolder {
        public TextView tvTitle;
        public TextView tvOverview;
        public TextView tvReleaseDate;
        public TextView tvUserRating;
        public ImageView ivPoster;

        public ViewHolder(View view) {
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvOverview = (TextView) view.findViewById(R.id.tvOverview);
            tvReleaseDate = (TextView) view.findViewById(R.id.tvReleaseDate);
            tvUserRating = (TextView) view.findViewById(R.id.tvUserRating);
            ivPoster = (ImageView) view.findViewById(R.id.ivPoster);
        }
    }
}
