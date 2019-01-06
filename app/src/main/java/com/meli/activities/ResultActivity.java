package com.meli.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Surface;
import android.view.WindowManager;

import com.meli.R;
import com.meli.adapters.ItemsAdapter;
import com.meli.entities.Item;
import com.meli.entities.SearchResponse;
import com.meli.network.API;
import com.meli.network.RestUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.meli.Parameters.QUERY;

public class ResultActivity extends AppCompatActivity {

    private Context mContext = this;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mItemsRecyclerView;

    private GridLayoutManager mGridLayoutManager;
    private ItemsAdapter mItemsAdapter;
    private List<Item> mItems;

    private int mPage = 0;
    private boolean mUpdating = true, mLastPage = false;

    private String mQuery;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int screenOrientation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (screenOrientation) {
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                mGridLayoutManager.setSpanCount(3);
                break;
            default:
                mGridLayoutManager.setSpanCount(2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle bundle = getIntent().getExtras();
        mQuery = bundle.getString(QUERY);

        initToolbar();
        initViews();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(mQuery);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initViews() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mItemsRecyclerView = findViewById(R.id.itemsRecyclerView);

        mItemsRecyclerView.setHasFixedSize(true);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mItemsRecyclerView.setLayoutManager(mGridLayoutManager);

        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            cleanItems();
        });

        mSwipeRefreshLayout.setOnRefreshListener(this::cleanItems);

        mItemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItem = mGridLayoutManager.getItemCount();
                int lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();

                if (!mUpdating && lastVisibleItem == totalItem - 1) {
                    if (mItems.size() > 0) {
                        if (!mLastPage) {
                            mUpdating = true;
                            getMore();
                        }
                    }
                }
            }
        });

    }

    private void cleanItems() {
        if (mItems != null) {
            mItems.clear();
            mItems = null;
        }
        if (mItemsRecyclerView.getAdapter() != null) {
            mItemsRecyclerView.getAdapter().notifyDataSetChanged();
            mItemsAdapter = null;
        }
        mItems = new ArrayList<>();
        updateSearch();
    }

    private void updateSearch() {

        mUpdating = true;
        mPage = 0;
        mLastPage = false;

        API client = RestUtil.getInstance().getRetrofit().create(API.class);

        Call<SearchResponse> call = client.search(mQuery, 10, mPage);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                if (response.body() != null && response.body().getResults() != null) {
                    if (response.body().getResults().size() > 0) {
                        mItems = response.body().getResults();
                        mItemsAdapter = new ItemsAdapter(mContext, mItems);
                        mItemsRecyclerView.setAdapter(mItemsAdapter);
                    } else {
                        mLastPage = true;
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
                mUpdating = false;
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                mUpdating = false;
            }
        });
    }

    private void getMore() {

        mPage++;

        API client = RestUtil.getInstance().getRetrofit().create(API.class);

        Call<SearchResponse> call = client.search(mQuery, 10, mPage);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                if (response.body() != null && response.body().getResults() != null) {
                    if (response.body().getResults().size() > 0) {
                        mItems.addAll(response.body().getResults());
                        mItemsAdapter.notifyDataSetChanged();
                    } else {
                        mLastPage = true;
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
                mUpdating = false;
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                mUpdating = false;
            }
        });
    }

}