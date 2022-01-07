package com.example.telefixmain.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.R;

public class VehicleItemViewHolder extends RecyclerView.ViewHolder {
    TextView vehicleTitle, vehicleColor;
    View mView;


    public VehicleItemViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        // initialize views price_item_slot.xml
        vehicleTitle = itemView.findViewById(R.id.tv_vehicle_title_in_vehicle_list);
        vehicleColor = itemView.findViewById(R.id.tv_price_value_in_vehicle_list);
    }
}