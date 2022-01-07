package com.example.telefixmain.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.R;

public class PriceItemViewHolder extends RecyclerView.ViewHolder {
    TextView serviceName, servicePrice;
    View mView;


    public PriceItemViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        // initialize views price_item_slot.xml
        serviceName = itemView.findViewById(R.id.tv_service_name_in_price_list);
        servicePrice = itemView.findViewById(R.id.tv_price_value_in_price_list);
    }
}