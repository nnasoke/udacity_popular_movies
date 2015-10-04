package com.groooo.android.popularmovie.ui.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
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
import com.groooo.android.popularmovie.helper.MdbContract;
import com.groooo.android.popularmovie.helper.MdbHelper;
import com.groooo.android.popularmovie.ui.activity.MovieDetailActivity;
import com.groooo.android.popularmovie.helper.Utility;

public class MovieFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    public static final int MOVIES_LOADER_ID = 8899;

    private MoviePosterAdapter mAdapter;

    private static final String[] SELECT_COLUMNS = new String[] {
            MdbContract.MovieEntry._ID,
            MdbContract.MovieEntry.COLUMN_ITEM_ID,
            MdbContract.MovieEntry.COLUMN_POSTER_PATH
    };

    // This returns the current sort order that relates to shared preference's setting.
    private String getSortOrderString() {
        final String currentSort = Utility.getSharedPreferenceSortOrder(getActivity());
        final String popularitySort = getString(R.string.pref_sort_by_most_popular);
        if (currentSort.equals(popularitySort))
            return String.format("%s DESC", MdbContract.MovieEntry.COLUMN_POPULARITY);
        else
            return String.format("%s DESC", MdbContract.MovieEntry.COLUMN_VOTE_AVERAGE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create default adapter without cursor.
        mAdapter = new MoviePosterAdapter(getActivity(), null, 0);

        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        GridView gv = (GridView)view.findViewById(R.id.movies_gridview);
        gv.setAdapter(mAdapter);
        gv.setOnItemClickListener(this);
        return view;
    }

    public void onSortChange() {
        getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);

        String sortOrder = Utility.getSharedPreferenceSortOrder(getActivity());
        FetchMoviesTask task = new FetchMoviesTask(getActivity());
        task.execute(sortOrder);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MdbContract.MovieEntry.CONTENT_URI,
                SELECT_COLUMNS,
                null,
                null,
                getSortOrderString());
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
        long itemId = cursor.getLong(cursor.getColumnIndex(MdbContract.MovieEntry.COLUMN_ITEM_ID));
        if (cursor != null) {
            Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
            intent.setData(MdbContract.MovieEntry.buildMoviesUri(itemId));
            startActivity(intent);
        }
    }

    // Class thar help fetching data in background.
    public class FetchMoviesTask extends AsyncTask<String, String, String> {
        Context mContext;

        public FetchMoviesTask(Context context) {
            mContext = context;
        }
        @Override
        protected String doInBackground(String... params) {
            final String sortBy = params.length > 0? params[0] : null;
            return MdbHelper.getJsonString(sortBy);
        }

        @Override
        protected void onPostExecute(String moviesJsonStr) {
            final ContentValues[] contentValues = MdbHelper.parseMoviesValue(moviesJsonStr);
            if (contentValues.length > 0) {
                mContext.getContentResolver()
                        .bulkInsert(MdbContract.MovieEntry.CONTENT_URI, contentValues);
            }
        }
    }
}
