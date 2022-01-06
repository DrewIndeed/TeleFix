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
    Context siteInfoActivity;
    ArrayList<HashMap<String, String>> infoHashMapList;

    public PriceListAdapter(Context siteInfoActivity, ArrayList<HashMap<String, String>> infoHashMapList) {
        this.siteInfoActivity = siteInfoActivity;
        this.infoHashMapList = infoHashMapList;
    }

    @NonNull
    @Override
    public PriceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.price_item_slot, parent, false);
        return new PriceItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceItemViewHolder holder, int position) {
        // binding views and set data
        holder.serviceName.setText(infoHashMapList.get(position).get("serviceName"));
        holder.servicePrice.setText(infoHashMapList.get(position).get("servicePrice"));
    }

    @Override
    public int getItemCount() {
        return infoHashMapList.size();
    }
}

