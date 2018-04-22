package com.beayeah.privalia.privaliaapp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.ConfigurationCompat;

import java.util.ArrayList;

import javax.annotation.ParametersAreNonnullByDefault;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerFragment extends Fragment {

    private String query;

    private MainActivity view;
    private MovieService service;
    private ArrayList<MovieResponse.Movie> filmList;
    private Callback<MovieResponse> filmListListener;
    private Callback<MovieResponse> searchListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        filmList = new ArrayList<>();
        setupListeners();
        service = new MovieService(view);
        getMoviesList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        view = (MainActivity) context;
        if (service != null) service.setView(view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        view = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.destroy();
        view = null;
        service = null;
    }

    public void getMoviesList() {
        if (!isConnected()) {
            view.hideRefreshLayout();
            return;
        }
        if (service.getPage() == 1) {
            view.hideRefreshLayout();
            view.showLoader();
        }
        if (ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0).toString().equals("es_ES")) {
            service.getMoviesListEs(filmListListener);
        } else {
            service.getMoviesList(filmListListener);
        }
    }

    public void searchMovieByKeyword(String query, boolean newSearch) {
        if (!isConnected()) {
            view.hideRefreshLayout();
            return;
        }
        if (newSearch) service.setPage(1);
        this.query = query;
        if (ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0).toString().equals("es_ES")) {
            service.searchMovieByKeywordEs(query, searchListener);
        } else {
            service.searchMovieByKeyword(query, searchListener);
        }
    }

    public void searchMovieByKeyword(String query) {
        searchMovieByKeyword(query, false);
    }

    public ArrayList<MovieResponse.Movie> getList() {
        return filmList;
    }

    public void cancelPendingRequests() {
        service.cancelPendingRequests();
    }

    private void setupListeners() {
        filmListListener = new Callback<MovieResponse>() {
            @Override
            @ParametersAreNonnullByDefault
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                view.hideLoader();
                view.hideRefreshLayout();

                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    ArrayList<MovieResponse.Movie> list = movieResponse.getResults();
                    filmList.addAll(list);
                    if (service.getPage() == 1)
                        view.setData(list);
                    else view.addItems(list);
                    if (!list.isEmpty())
                        service.setPage(service.getPage() + 1);
                } else
                    view.showResponseErrorForMovies();
            }

            @Override
            @ParametersAreNonnullByDefault
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                view.hideLoader();
                view.hideRefreshLayout();
            }
        };

        searchListener = new Callback<MovieResponse>() {
            @Override
            @ParametersAreNonnullByDefault
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                view.hideLoader();
                view.hideRefreshLayout();

                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    ArrayList<MovieResponse.Movie> list = movieResponse.getResults();
                    if (list.isEmpty() && service.getPage() > 1) {
                        view.showEmptyListError();
                        return;
                    } else if (service.getPage() == 1) view.setData(list);
                    else view.addItems(list);

                    if (!list.isEmpty()) service.setPage(service.getPage() + 1);
                    view.setTitle("Results for '" + query + "'");
                } else
                    view.showResponseErrorForSearch(query, service.getPage() == 1);
            }

            @Override
            @ParametersAreNonnullByDefault
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                view.hideLoader();
                view.hideRefreshLayout();
            }
        };
    }

    private boolean isConnected() {
        return Utils.isConnected((MainActivity) view);
    }
}
