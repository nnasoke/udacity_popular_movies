package com.groooo.android.popularmovie;

import android.test.AndroidTestCase;

import com.groooo.android.popularmovie.helper.MovieApi;

import org.json.JSONException;
import org.json.JSONObject;

public class MoviesDbApiTest extends AndroidTestCase {

    public void testPullingDataFromMovieDb() throws JSONException {
        final String jsonStr = MovieApi.getJsonString(mContext.getString(R.string.pref_sort_by_most_popular));
        JSONObject jsonObject = new JSONObject(jsonStr);
        assertEquals(jsonObject.length() > 0, true);

        JSONObject testObject = jsonObject.getJSONArray("results").getJSONObject(0);
        assertNotNull(testObject.getString("id"));
        assertNotNull(testObject.getString("title"));
        assertNotNull(testObject.getString("overview"));
        assertNotNull(testObject.getString("release_date"));
        assertNotNull(testObject.getString("poster_path"));
        assertNotNull(testObject.getString("backdrop_path"));
        assertNotNull(testObject.getString("popularity"));
        assertNotNull(testObject.getString("vote_average"));
    }
}
