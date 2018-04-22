package com.beayeah.privalia.privaliaapp;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_WORKER_FRAGMENT = "worker";
    @BindString(R.string.toolbar_title)
    String toolbarTitle;
    @BindString(R.string.search_hint)
    String searchHint;
    @BindString(R.string.network_error)
    String networkError;
    @BindString(R.string.response_error)
    String responseError;
    @BindString(R.string.no_more_results)
    String noMoreResults;
    @BindView(R.id.movie_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipyRefreshLayout mRefreshLayout;
    @BindView(R.id.loader)
    AVLoadingIndicatorView loader;
    @BindView(R.id.no_more_movies)
    TextView tvEmpty;
    @BindView(R.id.main_layout)
    RelativeLayout layout;
    @BindView(R.id.go_up)
    FloatingActionButton goUp;
    RecyclerView.LayoutManager mLayoutMngr;
    MovieAdapter mAdapter;
    WorkerFragment mWorkerFragment;
    String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setTitle(toolbarTitle);
        setupRecyclerView();
        addRefreshListener();

        FragmentManager fm = getSupportFragmentManager();
        mWorkerFragment = (WorkerFragment) fm.findFragmentByTag(TAG_WORKER_FRAGMENT);

        if (mWorkerFragment == null) {
            mWorkerFragment = new WorkerFragment();
            fm.beginTransaction().add(mWorkerFragment, TAG_WORKER_FRAGMENT).commit();
        } else {
            if (savedInstanceState.containsKey("query")) {
                query = savedInstanceState.getString("query");
            }
            if (savedInstanceState.containsKey("list")) {
                ArrayList<MovieResponse.Movie> list = (ArrayList<MovieResponse.Movie>)
                        savedInstanceState.getSerializable("list");
                setData(list);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            outState.putSerializable("list", mAdapter.getList());
        }
        if (query != null && !query.isEmpty()) {
            outState.putString("query", query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        final MenuItem searchItem = menu.findItem(R.id.item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(searchHint);

        if (query != null && !query.isEmpty()) {
            searchItem.expandActionView();
            searchView.setQuery(query, false);
            searchView.clearFocus();
        }

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setQuery("", false);
                if (!mWorkerFragment.getList().isEmpty()) {
                    mWorkerFragment.cancelPendingRequests();
                    setData(mWorkerFragment.getList());
                    setTitle(toolbarTitle);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals(query)) {
                    if (query.length() > 1 && newText.isEmpty()) return false;
                    query = newText;
                } else {
                    return false;
                }
                if (query.isEmpty() && !mWorkerFragment.getList().isEmpty()) {
                    mWorkerFragment.cancelPendingRequests();
                    setData(mWorkerFragment.getList());
                    setTitle(toolbarTitle);
                } else {
                    mWorkerFragment.searchMovieByKeyword(query, true);
                }
                return true;
            }
        });
        return true;
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void showEmptyText() {
        Resources res = this.getResources();
        tvEmpty.setText(String.format(res.getString(R.string.no_results_query), query));
        tvEmpty.setVisibility(View.VISIBLE);
    }

    private void hideEmptyText() {
        tvEmpty.setVisibility(View.GONE);
    }

    public void showLoader() {
        loader.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        loader.setVisibility(View.GONE);
    }

    public void hideRefreshLayout() {
        mRefreshLayout.setRefreshing(false);
    }

    public void showEmptyListError() {
        Utils.showSnackbar(layout, noMoreResults);
    }

    public void showResponseErrorForMovies() {
        Utils.showSnackbar(layout, responseError, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWorkerFragment.getMoviesList();
            }
        });
    }

    public void showResponseErrorForSearch(final String query, final boolean newSearch) {
        Utils.showSnackbar(layout, responseError, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWorkerFragment.searchMovieByKeyword(query, newSearch);
            }
        });
    }

    public void setData(ArrayList<MovieResponse.Movie> list) {
        mAdapter = new MovieAdapter(this, list);
        mRecyclerView.setAdapter(mAdapter);
        if (list.isEmpty()) showEmptyText();
        else hideEmptyText();
    }

    public void addItems(ArrayList<MovieResponse.Movie> list) {
        if (mAdapter != null)
            for (MovieResponse.Movie m : list)
                mAdapter.add(m);
    }

    private void setupRecyclerView() {
        mLayoutMngr = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutMngr);
    }

    private void addRefreshListener() {
        mRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (query != null && !query.isEmpty()) mWorkerFragment.searchMovieByKeyword(query);
                else mWorkerFragment.getMoviesList();
            }
        });
    }

    @OnClick(R.id.go_up)
    void OnUpClick() {
        mRecyclerView.smoothScrollToPosition(0);
    }
}
