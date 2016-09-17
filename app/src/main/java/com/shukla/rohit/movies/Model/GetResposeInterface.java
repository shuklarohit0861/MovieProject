package com.shukla.rohit.movies.Model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by thero on 15-09-2016.
 */
public interface GetResposeInterface {

    @GET("movie/top_rated")
    Call<MovieModel> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MovieModel> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<Result> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<VideoTrailer> getMovieTrailer(@Path("id") int id,@Query("api_key") String apiKey);

    @GET("movie/(id)/review")
    Call<MovieReviews> getMovieReview(@Path("id") int id,@Query("api_key") String apiKey);

}
