package com.groooo.android.popularmovie.helper;

import com.groooo.android.popularmovie.model.Movie;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MdbApiHelper {

    private static final String LOG_TAG = MdbApiHelper.class.getSimpleName();

    private static final String API_BASE_URL = "http://api.themoviedb.org/3";
    private static final String IMG_BASE_URL = "http://image.tmdb.org/t/p";
    private static final String API_KEY = "97bd78bb6d59be9b198e0265fb7af9ca";

    public static void updateMovies(String sort) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .build();
        MdbApiService apiService = retrofit.create(MdbApiService.class);
        Call<List<Movie>> moviesList = apiService.getMoviesList(API_KEY);
        moviesList.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Response<List<Movie>> response, Retrofit retrofit) {
                List<Movie> movies = response.body();
                // insert db here
            }

            @Override
            public void onFailure(Throwable t) {
                // raise error
            }
        });
    }
}
