package com.example.telefixmain.Activity.Mechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.content.DialogInterface;
import android.os.Handler;

import com.example.telefixmain.Adapter.SOSRequestListAdapter;
import com.example.telefixmain.Model.Booking.SOSRequest;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.BookingHandler;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SOSRequestActivity extends AppCompatActivity implements SOSRequestListAdapter.OnRequestListener {

    // firestore & realtime database & authentication
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseDatabase vendorsBookings = FirebaseDatabase.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();

    // xml
    private RecyclerView recyclerView;
    private SOSRequestListAdapter sosRequestAdapter;

    // data
    private ArrayList<SOSRequest> sosRequests = new ArrayList<>();
    private Location currentLocation = new Location("");
    // intent data receivers
    private User userTracker;
    private String vendorId;
    private String mechanicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_sos_request);

        // Retrieve mechanic info
        Intent intent = getIntent();
        userTracker = (User) intent.getSerializableExtra("loggedInUser");
        vendorId = userTracker.getVendorId();
        mechanicId = mUser.getUid();

        // Get vendor's location
        getVendorLocation();

        // recyclerview settings
        recyclerView = findViewById(R.id.sos_request_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        sosRequestAdapter = new SOSRequestListAdapter(SOSRequestActivity.this,
                this,
                currentLocation,
                sosRequests,
                vendorId,
                mechanicId);
        recyclerView.setAdapter(sosRequestAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        // listen for db reference
        DatabaseReference openSOSRequest = vendorsBookings.getReference().child(vendorId).child("sos").child("request");
        // set ValueEventListener that delay the onDataChange
        ValueEventListener openSOSRequestListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear current request list & add again
                sosRequests.clear();
                ArrayList<SOSRequest> tmp = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SOSRequest request = ds.getValue(SOSRequest.class);
                    if (Objects.requireNonNull(request).getMechanicId().equals("")) {
                        tmp.add(request);
                    }
                    System.out.println("FETCH REQUEST ___________________");
                }

                // Sort collections by time created
                Collections.sort(tmp, (o1, o2) -> Long.compare(o1.getTimestampCreated(), o2.getTimestampCreated()));

                sosRequests.addAll(tmp);
                sosRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        openSOSRequest.addValueEventListener(openSOSRequestListener);
    }

    /**
     * Method to get vendor location
     */
    private void getVendorLocation() {
        ArrayList<Vendor> tmp = new ArrayList<>();
        DatabaseHandler.getSingleVendor(db, userTracker.getVendorId(), tmp, () -> {
            currentLocation.setLatitude(Double.parseDouble(tmp.get(0).getLat()));
            currentLocation.setLongitude(Double.parseDouble(tmp.get(0).getLng()));
        });
    }

    @Override
    public void onRequestClick(int position) {
        String requestId = sosRequests.get(position).getRequestId();
        String customerId = sosRequests.get(position).getUserId();
        long startTime = sosRequests.get(position).getTimestampCreated();
        // Confirm accept SOS request
        AlertDialog.Builder builder = new AlertDialog.Builder(SOSRequestActivity.this);
        builder.setTitle("Confirm accept SOS request");
        builder.setMessage("Do you want to confirm helping this user?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                BookingHandler.acceptSOSRequest(vendorsBookings,
                        SOSRequestActivity.this,
                        vendorId,
                        sosRequests.get(position).getRequestId(),
                        mechanicId,
                        () -> {
                            // initialize progress tracking
                            long startProgressTracking = System.currentTimeMillis() / 1000L;
                            BookingHandler.createProgressTracking(
                                    vendorsBookings,
                                    SOSRequestActivity.this,
                                    vendorId,
                                    requestId,
                                    startProgressTracking, () -> {
                                        // Delay to make sure the progress has been initialized on db
                                        new Handler().postDelayed(() -> {
                                            Intent i = new Intent(SOSRequestActivity.this, SOSProgressActivity.class);
                                            i.putExtra("vendorId", vendorId);
                                            i.putExtra("requestId", requestId);
                                            i.putExtra("customerId", customerId);
                                            i.putExtra("startTime", startTime);
                                            startActivity(i);
                                        }, 3000);
                                    });
                        });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}