package com.meli.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.meli.R;
import com.meli.entities.Description;
import com.meli.entities.Item;
import com.meli.network.API;
import com.meli.network.RestUtil;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.meli.Parameters.ID;

public class ItemActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String mItemId;

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

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initViews() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            updateItem();
        });

        mSwipeRefreshLayout.setOnRefreshListener(this::updateItem);

    }

    private void updateItem() {

        API client = RestUtil.getInstance().getRetrofit().create(API.class);

        Call<Item> call = client.item(mItemId);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                if (response.body() != null) {
                    populateView(response.body());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

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

    private void updateDescription() {

        API client = RestUtil.getInstance().getRetrofit().create(API.class);

        Call<Description> call = client.description(mItemId);
        call.enqueue(new Callback<Description>() {
            @Override
            public void onResponse(@NonNull Call<Description> call, @NonNull Response<Description> response) {
                if (response.body() != null) {
                    TextView descriptionTextView = findViewById(R.id.descriptionTextView);
                    descriptionTextView.setText(response.body().getPlainText());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Description> call, @NonNull Throwable t) {
            }
        });
    }

}