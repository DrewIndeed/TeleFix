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

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleItemViewHolder> {
    Context vehicleInfoActivity;
    ArrayList<HashMap<String, String>> vehicleHashMapList;

    public VehicleListAdapter(Context vehicleInfoActivity, ArrayList<HashMap<String, String>> vehicleHashMapList) {
        this.vehicleInfoActivity = vehicleInfoActivity;
        this.vehicleHashMapList = vehicleHashMapList;
    }

    @NonNull
    @Override
    public VehicleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_vehicle_item, parent, false);
        return new VehicleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleItemViewHolder holder, int position) {
        // binding views and set data
        holder.vehicleTitle.setText(vehicleHashMapList.get(position).get("vehicleTitle"));
        holder.vehicleColor.setText(vehicleHashMapList.get(position).get("vehicleColor"));
        holder.vehicleNumberPlate.setText(vehicleHashMapList.get(position).get("vehicleNumberPlate"));
    }

    @Override
    public int getItemCount() {
        return vehicleHashMapList.size();
    }
}

