package com.example.telefixmain.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.Model.Booking.MaintenanceRequest;
import com.example.telefixmain.Model.Booking.SOSRequest;
import com.example.telefixmain.Model.Event;
import com.example.telefixmain.Model.EventTitle;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryItemViewHolder> {

    Context activityContext;
    ArrayList<EventTitle> eventTitles;

    // Firestore & realtime db & auth
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase vendorsBookings = FirebaseDatabase.getInstance();

    // intent data receivers
    User userTracker;

    public HistoryListAdapter(Context activityContext, ArrayList<EventTitle> eventTitles, User userTracker) {
        this.activityContext = activityContext;
        this.eventTitles = eventTitles;
        this.userTracker = userTracker;
    }

    @NonNull
    @Override
    public HistoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sos_request_item, parent, false);
        return new HistoryItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryItemViewHolder holder, int position) {
        holder.timestamp.setText(timestampConverter(eventTitles.get(position).getTimestamp()));
        holder.type.setText(eventTitles.get(position).getType());

        // Check user to display correctly
        if (userTracker.getIsMechanic().equals("true")) {
            // Mechanic session
            if (System.currentTimeMillis()/1000L < eventTitles.get(position).getTimestamp()) {
                holder.status.setTextColor(activityContext.getResources().getColor(R.color.orange));
                holder.status.setText("Accepted - Ongoing Request");
            }
            else {
                holder.status.setTextColor(activityContext.getResources().getColor(R.color.quantum_tealA400));
                holder.status.setText("Completed");
            }
        }
        else {
            // User session
            holder.type.setTextColor(activityContext.getResources().getColor(R.color.bmw_red));

            if (eventTitles.get(position).getStatus().equals("success")) {
                holder.status.setTextColor(activityContext.getResources().getColor(R.color.quantum_tealA400));
                holder.status.setText("Completed");
            }
            else {
                holder.status.setTextColor(activityContext.getResources().getColor(R.color.bmw_red));
                holder.status.setText("Rejected");
            }
        }

    }

    @Override
    public int getItemCount() {
        if (eventTitles != null) {
            return eventTitles.size();
        }
        return 0;
    }

    /**
     * Method to convert the unix timestamp to GMT
     */
    public static String timestampConverter(long unixValue) {
        Date date = new java.util.Date(unixValue * 1000L);
        // the format of your date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
        return sdf.format(date);
    }
}
