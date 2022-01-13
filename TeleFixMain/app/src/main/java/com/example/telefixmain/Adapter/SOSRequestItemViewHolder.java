package com.example.telefixmain.Adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.R;

public class SOSRequestItemViewHolder extends RecyclerView.ViewHolder {
    TextView userInfo, distance, timeCreated;
    LinearLayout layout;
    View mView;

    public SOSRequestItemViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        layout = itemView.findViewById(R.id.sos_request_viewholder);
        userInfo = itemView.findViewById(R.id.tv_sos_user_info);
        distance = itemView.findViewById(R.id.tv_sos_km_away);
        timeCreated = itemView.findViewById(R.id.tv_sos_time_created);
    }
}
