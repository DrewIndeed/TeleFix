package com.example.telefixmain.Adapter;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.R;

public class MaintenanceRequestItemViewHolder extends RecyclerView.ViewHolder {
    TextView title, status;
    EditText date, time;
    LinearLayout actionButtons;
    View mView;

    public MaintenanceRequestItemViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        title = itemView.findViewById(R.id.tv_maintenance_title);
        date = itemView.findViewById(R.id.edit_date_picker);
        time = itemView.findViewById(R.id.edit_time_picker);
        status = itemView.findViewById(R.id.tv_maintenance_status);
        actionButtons = itemView.findViewById(R.id.ll_maintenance_actions);
    }
}
