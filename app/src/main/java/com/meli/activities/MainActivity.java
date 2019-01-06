package com.meli.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.meli.R;

import static com.meli.Parameters.QUERY;

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

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        SearchView searchView = findViewById(R.id.searchView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
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

    private void initViews() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mItemsRecyclerView = findViewById(R.id.itemsRecyclerView);

        mItemsRecyclerView.setHasFixedSize(true);
        mItemsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

}