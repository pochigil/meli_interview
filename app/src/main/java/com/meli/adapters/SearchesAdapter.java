package com.meli.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meli.R;
import com.meli.activities.ResultActivity;

import java.util.List;

import model.Searches;

import static com.meli.Parameters.QUERY;

public class SearchesAdapter extends RecyclerView.Adapter<SearchesAdapter.ViewHolder> {

    private List<Searches> items;
    private Context context;

    public SearchesAdapter(Context context, List<Searches> items) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Searches item = items.get(position);

        holder.queryTextView.setText(item.getQuery());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ResultActivity.class);
            intent.putExtra(QUERY, item.getQuery());
            context.startActivity(intent);
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView queryTextView;

        private ViewHolder(View itemView) {
            super(itemView);
            queryTextView = itemView.findViewById(R.id.queryTextView);
        }
    }

}