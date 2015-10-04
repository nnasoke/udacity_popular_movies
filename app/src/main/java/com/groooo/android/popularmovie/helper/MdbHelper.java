package com.groooo.android.popularmovie.helper;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class MdbHelper {
    private static final String LOG_TAG = MdbHelper.class.getSimpleName();

    private static final Uri IMAGE_BASE_URI = Uri.parse("http://image.tmdb.org/t/p");
    private static final String IMG_SIZE_POSTER = "w185";
    private static final String IMG_SIZE_BACKDROP = "w500";

    private static final String MOVIE_API_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    private static final String API_KEY = "api_key";
    private static final String API_KEY_VALUE = "97bd78bb6d59be9b198e0265fb7af9ca";
    private static final String SORT_BY = "sort_by";
    private static final String SORT_BY_DEFAULT = "popularity.desc";

    public static final String JSON_RESULTS = "results";
    public static final String JSON_ITEM_ID = "id";
    public static final String JSON_TITLE = "title";
    public static final String JSON_OVERVIEW = "overview";
    public static final String JSON_RELEASE_DATE = "release_date";
    public static final String JSON_POPULARITY = "popularity";
    public static final String JSON_VOTE_AVERAGE = "vote_average";
    public static final String JSON_POSTER_PATH = "poster_path";

    public static final String SOURCE_DATE_FORMAT = "yyyy-mm-dd";

    public static String getJsonString(String sort) {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferReader = null;
        String jsonString = "";
        String sortBy = (sort != null && !sort.isEmpty())? sort : SORT_BY_DEFAULT;

        try {
            Uri uri = Uri.parse(MOVIE_API_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, API_KEY_VALUE)
                    .appendQueryParameter(SORT_BY, sortBy)
                    .build();
            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            bufferReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferReader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            jsonString = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bufferReader != null) {
                try {
                    bufferReader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream reader", e);
                }
            }
        }
        return  jsonString;
    }

    public static ContentValues[] parseMoviesValue(String jsonString) {
        Vector<ContentValues> cvValues = new Vector<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray moviesList = jsonObject.getJSONArray(JSON_RESULTS);
            cvValues = new Vector<>(moviesList.length());

            for (int i = 0; i < moviesList.length(); i++) {
                JSONObject movieObj = moviesList.getJSONObject(i);

                ContentValues cv = new ContentValues();
                cv.put(MdbContract.MovieEntry.COLUMN_ITEM_ID, movieObj.getString(JSON_ITEM_ID));
                cv.put(MdbContract.MovieEntry.COLUMN_TITLE, movieObj.getString(JSON_TITLE));
                cv.put(MdbContract.MovieEntry.COLUMN_OVERVIEW, movieObj.getString(JSON_OVERVIEW));
                cv.put(MdbContract.MovieEntry.COLUMN_RELEASE_DATE, Utility.dateToMilliseconds(SOURCE_DATE_FORMAT, movieObj.getString(JSON_RELEASE_DATE)));
                cv.put(MdbContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieObj.getString(JSON_VOTE_AVERAGE));
                cv.put(MdbContract.MovieEntry.COLUMN_POPULARITY, movieObj.getString(JSON_POPULARITY));
                cv.put(MdbContract.MovieEntry.COLUMN_POSTER_PATH, movieObj.getString(JSON_POSTER_PATH));
                cvValues.add(cv);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON", e);
        }
        return cvValues.toArray(new ContentValues[cvValues.size()]);
    }

    public static Uri getPosterImageUri(String imageFile) {
        final String slashOutStr = imageFile.replace("/", "");
        return IMAGE_BASE_URI.buildUpon()
                .appendPath(IMG_SIZE_POSTER)
                .appendPath(slashOutStr).build();
    }
}
