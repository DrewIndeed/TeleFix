package com.example.telefixmain.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PriceListAdapter extends RecyclerView.Adapter<PriceItemViewHolder> {
    Context priceInfoActivity;
    ArrayList<HashMap<String, String>> priceHashMapList;

    public PriceListAdapter(Context priceInfoActivity, ArrayList<HashMap<String, String>> priceHashMapList) {
        this.priceInfoActivity = priceInfoActivity;
        this.priceHashMapList = priceHashMapList;
    }

    @NonNull
    @Override
    public PriceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_price_item, parent, false);
        return new PriceItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceItemViewHolder holder, int position) {
        // binding views and set data
        holder.serviceName.setText(priceHashMapList.get(position).get("serviceName"));
        holder.servicePrice.setText(priceHashMapList.get(position).get("servicePrice"));
    }

    @Override
    public int getItemCount() {
        return priceHashMapList.size();
    }
}

