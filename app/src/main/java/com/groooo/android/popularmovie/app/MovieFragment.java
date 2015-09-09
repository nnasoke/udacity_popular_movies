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
import android.widget.AdapterView;
import android.widget.GridView;

import com.groooo.android.popularmovie.R;
import com.groooo.android.popularmovie.data.MovieContract;
import com.groooo.android.popularmovie.helper.FetchMoviesTask;
import com.groooo.android.popularmovie.helper.Utility;

public class MovieFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    public static final int MOVIES_LOADER_ID = 8899;

    private MoviePosterAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String sort = Utility.getSharedPreferenceSortOrder(getActivity());
        final Uri sortUri = sort.contains(getString(R.string.pref_sort_by_most_popular))?
                MovieContract.MovieEntry.buildMoviesOrderByMostPopular() :
                MovieContract.MovieEntry.buildMoviesOrderByHighestRate();

        Cursor cursor = getActivity().getContentResolver()
                .query(sortUri, null, null, null, null);
        mAdapter = new MoviePosterAdapter(getActivity(), cursor, 0);

        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        GridView gv = (GridView)view.findViewById(R.id.movies_gridview);
        gv.setAdapter(mAdapter);
        gv.setOnItemClickListener(this);
        return view;
    }

    public void onSortChange(String sort) {
        FetchMoviesTask task = new FetchMoviesTask(getActivity());
        task.execute(sort);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String sort = Utility.getSharedPreferenceSortOrder(getActivity());
        final Uri sortUri = sort.contains(getString(R.string.pref_sort_by_most_popular))?
                MovieContract.MovieEntry.buildMoviesOrderByMostPopular() :
                MovieContract.MovieEntry.buildMoviesOrderByHighestRate();
        return new CursorLoader(getActivity(), sortUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        long itemId = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ITEM_ID));
        if (cursor != null) {
            Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
            intent.setData(MovieContract.MovieEntry.buildMoviesUri(itemId));
            startActivity(intent);
        }
    }
}
