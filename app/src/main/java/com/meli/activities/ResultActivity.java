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
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meli.R;
import com.meli.adapters.ItemsAdapter;
import com.meli.entities.Item;
import com.meli.entities.SearchResponse;
import com.meli.network.API;
import com.meli.network.ServiceGenerator;
import com.meli.util.APIError;
import com.meli.util.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.meli.Parameters.QUERY;

/**
 * Activity will show a list of products based on a query
 */
public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";

    private Context mContext = this;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mItemsRecyclerView;

    //ConnectionLayout
    private LinearLayout mConnectionLinearLayout;

    private GridLayoutManager mGridLayoutManager;
    private ItemsAdapter mItemsAdapter;
    private List<Item> mItems;

    private int mPage = 0;
    private boolean mUpdating = true, mLastPage = false;

    private String mQuery;

    /**
     * When mobile is rotated spanCount changes to acomodate a better viewe
     *
     * @param newConfig
     */
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

    /**
     * We received the query from a previous Activity in order to get a list of products
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle bundle = getIntent().getExtras();
        mQuery = bundle.getString(QUERY);

        initToolbar();
        initViews();
    }

    /**
     * Init components on toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(mQuery);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
    }

    /**
     * On setDisplayHomeAsUpEnabled this method will be called, we used to it to go back to previous activity
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Initialization of views for the Activity
     * Setup of RecyclerView
     * Setup of SwipeRefreshLayout
     */
    private void initViews() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mItemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        mConnectionLinearLayout = findViewById(R.id.connectionLinearLayout);

        mItemsRecyclerView.setHasFixedSize(true);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mItemsRecyclerView.setLayoutManager(mGridLayoutManager);

        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            cleanItems();
        });

        mSwipeRefreshLayout.setOnRefreshListener(this::cleanItems);

        //Used for pagination
        //When reaching lastVisibleItem we call to get more data, but only if we are actually we are not already updating or there is
        // still more data to fetch
        mItemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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

    /**
     * Clear previous data from recyclerView
     */
    private void cleanItems() {
        mConnectionLinearLayout.setVisibility(LinearLayout.GONE);
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

    /**
     * API Call that will retrieve first page products
     */
    private void updateSearch() {

        mUpdating = true;
        mPage = 0;
        mLastPage = false;

        API client = ServiceGenerator.createService(API.class);

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
                        Toast.makeText(mContext, R.string.no_results, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG, error.message());
                    showConnectionError();
                }
                mSwipeRefreshLayout.setRefreshing(false);
                mUpdating = false;
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                mUpdating = false;
                showConnectionError();
            }
        });

    }

    /**
     * API Call that will retrieve next page in order
     */
    private void getMore() {

        mPage++;

        API client = ServiceGenerator.createService(API.class);

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
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG, error.message());
                }
                mSwipeRefreshLayout.setRefreshing(false);
                mUpdating = false;
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                mUpdating = false;
            }
        });
    }

    /**
     * Method for showing up a connection error view
     */
    private void showConnectionError() {
        mConnectionLinearLayout.setVisibility(LinearLayout.VISIBLE);

        TextView retryTextView = mConnectionLinearLayout.findViewById(R.id.retryTextView);

        retryTextView.setOnClickListener(view -> {
            mSwipeRefreshLayout.post(() -> {
                mSwipeRefreshLayout.setRefreshing(true);
                cleanItems();
            });

        });
    }

}