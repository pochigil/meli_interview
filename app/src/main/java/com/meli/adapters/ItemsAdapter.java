package com.meli.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meli.R;
import com.meli.activities.ItemActivity;
import com.meli.entities.Item;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.meli.Parameters.ID;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private List<Item> items;
    private Context context;

    public ItemsAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Item item = items.get(position);

        Picasso.get().load(item.getThumbnail()).into(holder.photoImageView);

        holder.nameTextView.setText(item.getTitle());
        holder.priceTextView.setText(String.format(context.getString(R.string.currency), item.getPrice()));

        if (item.getShipping().isFreeShipping())
            holder.shippingTextView.setVisibility(TextView.VISIBLE);
        else
            holder.shippingTextView.setVisibility(TextView.GONE);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ItemActivity.class);
            intent.putExtra(ID, item.getId());
            context.startActivity(intent);
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView photoImageView;
        private TextView nameTextView, priceTextView, shippingTextView;

        private ViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            shippingTextView = itemView.findViewById(R.id.shippingTextView);
        }
    }

}