package com.example.telefixmain.Adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.R;

public class HistoryItemViewHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView timestamp, type, status;
    LinearLayout layout;

    public HistoryItemViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        layout = itemView.findViewById(R.id.sos_request_viewholder);
        timestamp = itemView.findViewById(R.id.tv_sos_user_info);
        type = itemView.findViewById(R.id.tv_sos_km_away);
        status = itemView.findViewById(R.id.tv_sos_time_created);
    }
}
