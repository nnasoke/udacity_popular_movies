package com.groooo.android.popularmovie.helper;

import com.groooo.android.popularmovie.model.Movie;
import com.groooo.android.popularmovie.model.Review;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MdbApiService {
    @GET("/discover/movie")
    Call<List<Movie>> getMoviesList(@Query("api_key") String apiKey);

    @GET("/movie/{id}/reviews")
    Call<List<Review>> getReviewsList(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("/movie/{id}/videos")
    Call<List<Review>> getVideosList(@Path("id") String id, @Query("api_key") String apiKey);
}
