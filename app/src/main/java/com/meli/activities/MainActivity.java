package com.meli.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.meli.R;
import com.meli.adapters.SearchesAdapter;
import com.meli.services.DatabaseServices;

import java.util.List;

import model.Searches;

import static com.meli.Parameters.QUERY;

/**
 * Activity will provides a SearchView for searching products and a list of previous searches made
 */
public class MainActivity extends AppCompatActivity {

    private Context mContext = this;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mItemsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initViews();
    }

    /**
     * I
     * nit components on toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        SearchView searchView = findViewById(R.id.searchView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                DatabaseServices db = new DatabaseServices();
                db.addRequest(s);

                Intent intent = new Intent(mContext, ResultActivity.class);
                intent.putExtra(QUERY, s);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    /**
     * Initialization of views for the Activity
     * Setup of RecyclerView
     * Setup of SwipeRefreshLayout
     */
    private void initViews() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mItemsRecyclerView = findViewById(R.id.itemsRecyclerView);

        mItemsRecyclerView.setHasFixedSize(true);
        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mItemsRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));

        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            loadSearches();
        });

        mSwipeRefreshLayout.setOnRefreshListener(this::loadSearches);
    }

    /**
     * AsyncTask for fetching data from database
     */
    @SuppressLint("StaticFieldLeak")
    private void loadSearches() {
        new AsyncTask<Void, Void, List<Searches>>() {

            @Override
            protected List<Searches> doInBackground(Void... voids) {
                return new DatabaseServices().getSearches();
            }

            @Override
            protected void onPostExecute(List<Searches> searches) {
                super.onPostExecute(searches);
                SearchesAdapter searchesAdapter = new SearchesAdapter(mContext, searches);
                mItemsRecyclerView.setAdapter(searchesAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.execute();
    }

    /**
     * Everytime onResume is called searches are loaded again
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mItemsRecyclerView != null)
            loadSearches();
    }
}