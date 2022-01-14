package com.example.telefixmain.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.Model.Booking.MaintenanceRequest;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MaintenanceRequestListAdapter extends RecyclerView.Adapter<MaintenanceRequestItemViewHolder> {
    private FirebaseDatabase vendorBookings = FirebaseDatabase.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Context activityContext;
    ArrayList<MaintenanceRequest> maintenanceRequests;

    public MaintenanceRequestListAdapter(Context activityContext, ArrayList<MaintenanceRequest> maintenanceRequests) {
        this.activityContext = activityContext;
        this.maintenanceRequests = maintenanceRequests;
    }

    @NonNull
    @Override
    public MaintenanceRequestItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.center_dialog_maintenance_booking, parent, false);
        return new MaintenanceRequestItemViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MaintenanceRequestItemViewHolder holder, int position) {
        holder.actionButtons.setVisibility(View.GONE);

        String userId = maintenanceRequests.get(position).getUserId();
        long dtValue = maintenanceRequests.get(position).getDatetime();
        String dtString = timeConverter(dtValue);
        String[] dtArray = dtString.split("\\s");

        // get customer info (name + phone) and set text on mechanic screen
        ArrayList<User> tmp = new ArrayList<>();
        DatabaseHandler.getSingleUser(db, userId, tmp, () -> {
            holder.title.setText(tmp.get(0).getName() + "\n" + tmp.get(0).getPhone());
        });

        holder.status.setVisibility(View.VISIBLE);
        holder.status.setText("Waiting for acceptance");

        holder.date.setText(dtArray[0]);
        holder.time.setText(dtArray[1]);

        holder.date.setEnabled(false);
        holder.time.setEnabled(false);

    }

    @Override
    public int getItemCount() {
        if (maintenanceRequests != null) {
            return maintenanceRequests.size();
        }
        return 0;
    }

    /**
     * Method to convert the unix timestamp to GMT
     */

    public static String timeConverter(long unixValue) {
        Date date = new java.util.Date(unixValue * 1000L);
        // the format of your date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyy HH:mm");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
        return sdf.format(date);
    }
}
