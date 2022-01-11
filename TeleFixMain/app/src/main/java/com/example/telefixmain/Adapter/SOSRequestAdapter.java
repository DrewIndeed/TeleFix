package com.example.telefixmain.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.Activity.Customer.RequestProcessingActivity;
import com.example.telefixmain.Activity.Customer.SosActivity;
import com.example.telefixmain.Activity.Mechanic.SOSProgressActivity;
import com.example.telefixmain.Model.Booking.SOSRequest;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.BookingHandler;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SOSRequestAdapter extends RecyclerView.Adapter<SOSRequestViewHolder>{
    private Context activityContext;
    private ArrayList<SOSRequest> sosRequests;
    private Location currentLocation;

    // firestore & realtime db & auth
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase vendorsBookings = FirebaseDatabase.getInstance();

    // info id
    private String vendorId;
    private String mechanicId;

    // Click
    private OnRequestListener mOnRequestListener;

    public SOSRequestAdapter(Context activityContext,
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
    public SOSRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sos_request_item, parent, false);
        return new SOSRequestViewHolder(itemView, mOnRequestListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SOSRequestViewHolder holder, int position) {
        // retrieve request info on database
        String requestId = sosRequests.get(position).getRequestId();
        String userId = sosRequests.get(position).getUserId();
        double userLat = sosRequests.get(position).getLat();
        double userLng = sosRequests.get(position).getLng();
        long timeCreated = sosRequests.get(position).getTimestampCreated();

        // calculate distance away
        String distanceAwayValue = Double.toString(
                Math.round(getDistanceFromCurrentLocation(
                        userLat, currentLocation.getLatitude(),
                        userLng, currentLocation.getLongitude()) * 100.0) / 100.0);

        // get customer info (name + phone) and set text on mechanic screen
        ArrayList<User> tmp = new ArrayList<>();
        DatabaseHandler.getSingleUser(db, userId, tmp, () -> {
            holder.userInfo.setText(tmp.get(0).getName() + ": " + tmp.get(0).getPhone());
            holder.timeCreated.setText("Created time: " + timestampConverter(timeCreated));
            holder.distance.setText(distanceAwayValue + " km away");
        });

//        holder.layout.setOnClickListener(view -> {
//            // Confirm accept SOS request
//            AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
//            builder.setTitle("Confirm accept SOS request");
//            builder.setMessage("Do you want to confirm helping this user?");
//            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    BookingHandler.acceptSOSRequest(vendorsBookings,
//                            activityContext,
//                            vendorId,
//                            requestId,
//                            mechanicId,
//                            () -> {
//                        // initialize progress tracking
//                        long startProgressTracking = System.currentTimeMillis() / 1000L;
//                        BookingHandler.createProgressTracking(
//                                vendorsBookings,
//                                activityContext,
//                                vendorId,
//                                requestId,
//                                startProgressTracking, () -> {
//                                    // Delay to make sure the progress has been initialized on db
//                                    new Handler().postDelayed(() -> {
//                                        Intent i = new Intent(activityContext, SOSProgressActivity.class);
//                                        i.putExtra("vendorId", vendorId);
//                                        i.putExtra("requestId", requestId);
//                                        activityContext.startActivity(i);
//                                    }, 3000);
//                                });
//                    });
//
//                }
//            });
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    dialog.dismiss();
//                }
//            });
//            AlertDialog alert = builder.create();
//            alert.show();
//        });
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
    public static String timestampConverter (long unixValue) {
        Date date = new java.util.Date(unixValue*1000L);
        // the format of your date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-5"));
        return sdf.format(date);
    }

    /**
     * Method to calculate distance between 2 locations based on latitude and longitude
     */
    public static double getDistanceFromCurrentLocation(double lat1, double lat2,
                                                        double lng1, double lng2) {
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lng1 = Math.toRadians(lng1);
        lng2 = Math.toRadians(lng2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dLong = lng2 - lng1;
        double dLat = lat2 - lat1;
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dLong / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }

    public interface OnRequestListener {
        void onRequestClick(int position);
    }
}
