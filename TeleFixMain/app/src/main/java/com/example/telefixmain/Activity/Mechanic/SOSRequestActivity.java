package com.example.telefixmain.Activity.Mechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.example.telefixmain.Adapter.SOSRequestAdapter;
import com.example.telefixmain.Model.Booking.SOSRequest;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.R;
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

public class SOSRequestActivity extends AppCompatActivity {

    // firestore & realtime database & authentication
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseDatabase vendorsBookings = FirebaseDatabase.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();

    // xml
    private RecyclerView recyclerView;
    private SOSRequestAdapter sosRequestAdapter;

    // data
    private ArrayList<SOSRequest> sosRequests = new ArrayList<>();
    private Location currentLocation = new Location("");
    // intent data receivers
    private User userTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_sos_request);

        // Retrieve mechanic info
        Intent intent = getIntent();
        userTracker = (User) intent.getSerializableExtra("loggedInUser");

        // Get vendor's location
        getVendorLocation();

        // recyclerview settings
        recyclerView = findViewById(R.id.sos_request_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        sosRequestAdapter = new SOSRequestAdapter(SOSRequestActivity.this,
                currentLocation,
                sosRequests,
                userTracker.getVendorId(),
                mUser.getUid());
        recyclerView.setAdapter(sosRequestAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        // listen for db reference
        DatabaseReference openSOSRequest = vendorsBookings.getReference().child(userTracker.getVendorId()).child("sos").child("request");
        // set ValueEventListener that delay the onDataChange
        ValueEventListener openSOSRequestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear current request list & add again
                sosRequests.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SOSRequest request = ds.getValue(SOSRequest.class);
                    sosRequests.add(request);
                    System.out.println("FETCH REQUEST ___________________");
                }
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
}