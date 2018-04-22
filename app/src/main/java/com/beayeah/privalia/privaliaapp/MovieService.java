package com.beayeah.privalia.privaliaapp;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieService {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private int pg;
    private MainActivity view;
    private Retrofit retrofit;
    private MovieDbApi api;
    private Call<MovieResponse> currentCall;

    public MovieService(MainActivity view) {
        this.pg = 1;
        this.view = view;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();

        api = retrofit.create(MovieDbApi.class);
    }

    public void destroy() {
        view = null;
        retrofit = null;
        api = null;
        currentCall = null;
    }

    public void getMoviesList(Callback<MovieResponse> callback) {
        cancelPendingRequests();
        currentCall = api.getMoviesList(pg);
        currentCall.enqueue(callback);
    }

    public void getMoviesListEs(Callback<MovieResponse> callback) {
        cancelPendingRequests();
        currentCall = api.getMoviesListEs(pg);
        currentCall.enqueue(callback);
    }

    public void searchMovieByKeyword(final String query, Callback<MovieResponse> callback) {
        cancelPendingRequests();
        currentCall = api.searchMovieByKeyword(query, pg);
        currentCall.enqueue(callback);
    }

    public void searchMovieByKeywordEs(final String query, Callback<MovieResponse> callback) {
        cancelPendingRequests();
        currentCall = api.searchMovieByKeywordEs(query, pg);
        currentCall.enqueue(callback);
    }

    public int getPage() {
        return pg;
    }

    public void setPage(int pg) {
        this.pg = pg;
    }

    public void setView(MainActivity view) {
        this.view = view;
    }

    public void cancelPendingRequests() {
        if (currentCall != null) {
            currentCall.cancel();
            currentCall = null;
        }
    }
}
