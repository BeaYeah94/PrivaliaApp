package com.beayeah.privalia.privaliaapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface MovieDbApi {

    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("movie/popular?api_key=93aea0c77bc168d8bbce3918cefefa45")
    Call<MovieResponse> getMoviesList(@Query("page") int pg);

    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("search/movie?api_key=93aea0c77bc168d8bbce3918cefefa45")
    Call<MovieResponse> searchMovieByKeyword(@Query("query") String query, @Query("page") int pg);

    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("movie/popular?api_key=93aea0c77bc168d8bbce3918cefefa45&language=es")
    Call<MovieResponse> getMoviesListEs(@Query("page") int pg);

    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("search/movie?api_key=93aea0c77bc168d8bbce3918cefefa45&language=es")
    Call<MovieResponse> searchMovieByKeywordEs(@Query("query") String query, @Query("page") int pg);

}
