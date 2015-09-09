package com.groooo.android.popularmovie.helper;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.groooo.android.popularmovie.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class MovieApi {
    private static final String LOG_TAG = MovieApi.class.getSimpleName();

    private static final Uri IMAGE_BASE_URI = Uri.parse("http://image.tmdb.org/t/p");
    private static final String IMG_SIZE_POSTER = "w185";
    private static final String IMG_SIZE_BACKDROP = "w500";

    private static final String MOVIE_API_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    private static final String API_KEY = "api_key";
    private static final String API_KEY_VALUE = "[Need your key right here]";
    private static final String SORT_BY = "sort_by";
    private static final String SORT_BY_DEFAULT = "popularity.desc";

    private static final String JSON_RESULTS = "results";
    private static final String JSON_ITEM_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_RELEASE_DATE = "release_date";
    private static final String JSON_POPULARITY = "popularity";
    private static final String JSON_VOTE_AVERAGE = "vote_average";
    private static final String JSON_POSTER_PATH = "poster_path";

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

    public static ContentValues[] parseMoviesValue(String jsonString){
        Vector<ContentValues> cvValues = null;
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-mm-dd");
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray moviesList = jsonObject.getJSONArray(JSON_RESULTS);
            cvValues = new Vector<>(moviesList.length());

            for (int i = 0; i < moviesList.length(); i++) {
                JSONObject movieObj = moviesList.getJSONObject(i);

                ContentValues cv = new ContentValues();
                cv.put(MovieContract.MovieEntry.COLUMN_ITEM_ID, movieObj.getString(JSON_ITEM_ID));
                cv.put(MovieContract.MovieEntry.COLUMN_TITLE, movieObj.getString(JSON_TITLE));
                cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieObj.getString(JSON_OVERVIEW));
                cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, dateFmt.parse(movieObj.getString(JSON_RELEASE_DATE)).getTime());
                cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieObj.getString(JSON_VOTE_AVERAGE));
                cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movieObj.getString(JSON_POPULARITY));
                cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movieObj.getString(JSON_POSTER_PATH));
                cvValues.add(cv);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON", e);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing released date", e);
        }
        return cvValues.toArray(new ContentValues[cvValues.size()]);
    }

    public static Uri getPosterImageUri(String imageFile) {
        final String slashOutStr = imageFile.replace("/", "");
        return IMAGE_BASE_URI.buildUpon()
                .appendPath(IMG_SIZE_POSTER)
                .appendPath(slashOutStr).build();
    }

    public static Uri getBackdropImageUri(String imageFile) {
        final String slashOutStr = imageFile.replace("/", "");
        return IMAGE_BASE_URI.buildUpon()
                .appendPath(IMG_SIZE_BACKDROP)
                .appendPath(slashOutStr).build();
    }
}
