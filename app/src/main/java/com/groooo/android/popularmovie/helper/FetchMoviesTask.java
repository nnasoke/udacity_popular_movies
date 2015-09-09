package com.groooo.android.popularmovie.helper;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.groooo.android.popularmovie.data.MovieContract;

public class FetchMoviesTask extends AsyncTask<String, String, String> {
    Context mContext;

    public FetchMoviesTask(Context context) {
        mContext = context;
    }
    @Override
    protected String doInBackground(String... params) {
        final String sortBy = params.length > 0? params[0] : null;
        return MovieApi.getJsonString(sortBy);
    }

    @Override
    protected void onPostExecute(String moviesJsonStr) {
        final ContentValues[] contentValues = MovieApi.parseMoviesValue(moviesJsonStr);
        if (contentValues.length > 0) {
            mContext.getContentResolver()
                    .bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        }
    }
}