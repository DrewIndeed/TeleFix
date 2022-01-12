package com.example.telefixmain.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.Activity.Customer.SosActivity;
import com.example.telefixmain.Model.Booking.SOSRequest;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SOSRequestListAdapter extends RecyclerView.Adapter<SOSRequestItemViewHolder>{
    Context activityContext;
    ArrayList<SOSRequest> sosRequests;
    Location currentLocation;

    // Firestore & realtime db & auth
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // info id
    String vendorId;
    String mechanicId;

    // Click
    OnRequestListener mOnRequestListener;

    public SOSRequestListAdapter(Context activityContext,
                                 OnRequestListener mOnRequestListener,
                                 Location currentLocation,
                                 ArrayList<SOSRequest> sosRequests,
                                 String vendorId,
                                 String mechanicId) {
        this.activityContext = activityContext;
        this.currentLocation = currentLocation;
        this.sosRequests = sosRequests;
        this.vendorId = vendorId;
        this.mechanicId = mechanicId;
        this.mOnRequestListener = mOnRequestListener;
    }

    @NonNull
    @Override
    public SOSRequestItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sos_request_item, parent, false);
        return new SOSRequestItemViewHolder(itemView, mOnRequestListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SOSRequestItemViewHolder holder, int position) {
        // retrieve request info on database
        // String requestId = sosRequests.get(position).getRequestId();
        String userId = sosRequests.get(position).getUserId();
        double userLat = sosRequests.get(position).getLat();
        double userLng = sosRequests.get(position).getLng();
        long timeCreated = sosRequests.get(position).getTimestampCreated();

        // calculate distance away
        String distanceAwayValue = Double.toString(
                Math.round(SosActivity.getDistanceFromCurrentLocation(
                        userLat, currentLocation.getLatitude(),
                        userLng, currentLocation.getLongitude()) * 100.0) / 100.0);

        // get customer info (name + phone) and set text on mechanic screen
        ArrayList<User> tmp = new ArrayList<>();
        DatabaseHandler.getSingleUser(db, userId, tmp, () -> {
            holder.userInfo.setText(tmp.get(0).getName() + " - " + tmp.get(0).getPhone());
            holder.timeCreated.setText("Requested at: " + timestampConverter(timeCreated));
            holder.distance.setText(distanceAwayValue + " km away");
        });
    }

    @Override
    public int getItemCount() {
        if (sosRequests != null) {
            return sosRequests.size();
        }
        return 0;
    }

    /**
     * Method to convert the unix timestamp to GMT
     */
    public static String timestampConverter(long unixValue) {
        Date date = new java.util.Date(unixValue * 1000L);
        // the format of your date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-5"));
        return sdf.format(date);
    }

    public interface OnRequestListener {
        void onRequestClick(int position);
    }
}
