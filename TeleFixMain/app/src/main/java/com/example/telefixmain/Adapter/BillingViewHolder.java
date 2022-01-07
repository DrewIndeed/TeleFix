package com.example.telefixmain.Adapter;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.R;

public class BillingViewHolder extends RecyclerView.ViewHolder {
    TextView item, quantity, total;
    View mView;

    public BillingViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        item = itemView.findViewById(R.id.row_billing_item);
        quantity = itemView.findViewById(R.id.row_billing_quantity);
        total = itemView.findViewById(R.id.row_billing_total);
    }
}
