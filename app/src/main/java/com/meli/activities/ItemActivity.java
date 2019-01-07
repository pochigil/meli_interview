package com.meli.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meli.R;
import com.meli.entities.Description;
import com.meli.entities.Item;
import com.meli.network.API;
import com.meli.network.ServiceGenerator;
import com.meli.util.APIError;
import com.meli.util.ErrorUtils;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.meli.Parameters.ID;

/**
 * Activity will show information about the product
 */
public class ItemActivity extends AppCompatActivity {

    private static final String TAG = "ItemActivity";

    private SwipeRefreshLayout mSwipeRefreshLayout;

    //ConnectionLayout
    private LinearLayout mConnectionLinearLayout;

    private String mItemId;

    /**
     * We received the product id from a previous Activity in order to get the information of the product that we choose
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mItemId = bundle.getString(ID);

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
        getSupportActionBar().setTitle(null);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
    }

    /**
     * On setDisplayHomeAsUpEnabled this method will be called, we used to it to go back to previous activity
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Initialization of views for the Activity
     * Setup of SwipeRefreshLayout
     */
    private void initViews() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mConnectionLinearLayout = findViewById(R.id.connectionLinearLayout);

        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            updateItem();
        });

        mSwipeRefreshLayout.setOnRefreshListener(this::updateItem);

    }

    /**
     * API Call that will retrieve information about the product
     */
    private void updateItem() {

        API client = ServiceGenerator.createService(API.class);

        Call<Item> call = client.item(mItemId);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    populateView(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG, error.message());
                    showConnectionError();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                showConnectionError();
            }
        });
    }

    /**
     * After API call we populate the view with the received information
     * @param item
     */
    private void populateView(Item item) {
        getSupportActionBar().setTitle(item.getTitle());

        ImageView photoImageView = findViewById(R.id.photoImageView);
        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView priceTextView = findViewById(R.id.priceTextView);
        TextView shippingTextView = findViewById(R.id.shippingTextView);

        Picasso.get().load(item.getPhoto()).into(photoImageView);
        nameTextView.setText(item.getTitle());
        priceTextView.setText(String.format(getString(R.string.currency), item.getPrice()));

        if (item.getShipping().isFreeShipping())
            shippingTextView.setVisibility(TextView.VISIBLE);
        else
            shippingTextView.setVisibility(TextView.GONE);

        updateDescription();
    }

    /**
     * API Call for receiving description of the product, we then populate that description on descriptionTextView
     */
    private void updateDescription() {

        API client = ServiceGenerator.createService(API.class);

        Call<Description> call = client.description(mItemId);
        call.enqueue(new Callback<Description>() {
            @Override
            public void onResponse(@NonNull Call<Description> call, @NonNull Response<Description> response) {
                if (response.isSuccessful()) {
                    TextView descriptionTextView = findViewById(R.id.descriptionTextView);
                    assert response.body() != null;
                    descriptionTextView.setText(response.body().getPlainText());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG, error.message());
                    showConnectionError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Description> call, @NonNull Throwable t) {
                showConnectionError();
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
                updateItem();
            });
        });
    }

}