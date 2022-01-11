package com.example.telefixmain.Adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.R;

public class SOSRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView userInfo, distance, timeCreated;
    ConstraintLayout layout;
    View mView;
    SOSRequestAdapter.OnRequestListener onRequestListener;

    public SOSRequestViewHolder(@NonNull View itemView, SOSRequestAdapter.OnRequestListener onRequestListener) {
        super(itemView);
        mView = itemView;

        layout = itemView.findViewById(R.id.sos_request_viewholder);
        userInfo = itemView.findViewById(R.id.tv_sos_user_info);
        distance = itemView.findViewById(R.id.tv_sos_km_away);
        timeCreated = itemView.findViewById(R.id.tv_sos_time_created);
        this.onRequestListener = onRequestListener;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onRequestListener.onRequestClick(getAdapterPosition());
    }
}
