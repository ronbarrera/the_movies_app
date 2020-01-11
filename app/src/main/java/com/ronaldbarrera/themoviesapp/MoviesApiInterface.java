package com.ronaldbarrera.themoviesapp;

import com.ronaldbarrera.themoviesapp.model.MoviesModel;
import com.ronaldbarrera.themoviesapp.model.ReviewsModel;
import com.ronaldbarrera.themoviesapp.model.VideosModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesApiInterface {

    @GET("movie/popular")
    Call<MoviesModel> getPopularMovies(@Query("page") int page, @Query("api_key") String key);

    @GET("movie/top_rated")
    Call<MoviesModel> getTopRatedMovies(@Query("page") int page, @Query("api_key") String key);

    @GET("movie/{filter_by}")
    Call<MoviesModel> getMovies(@Path("filter_by") String filterBy, @Query("page") int page, @Query("api_key") String key);

    @GET("movie/{movie_id}/videos")
    Call<VideosModel> getMovieVideos(@Path("movie_id") int id, @Query("api_key") String key);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewsModel> getMovieReviews(@Path("movie_id") int id, @Query("api_key") String key);
}
