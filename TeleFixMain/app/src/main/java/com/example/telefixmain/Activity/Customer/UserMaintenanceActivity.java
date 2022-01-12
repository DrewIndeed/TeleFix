package com.example.telefixmain.Activity.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;

import com.example.telefixmain.Activity.Mechanic.SOSRequestActivity;
import com.example.telefixmain.Adapter.SOSRequestListAdapter;
import com.example.telefixmain.Adapter.VendorListAdapter;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class UserMaintenanceActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Vendor> vendors = new ArrayList<>();

    private RecyclerView recyclerView;
    private VendorListAdapter vendorListAdapter;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationClient;

    private SearchView searchView;
    private User userTracker;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maintenance);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Search for vendor:");
        // get data from intent sent from Login Activity
        Intent intent = getIntent();
        userTracker = (User) intent.getSerializableExtra("loggedInUser");

        // create a get fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // checking for user's permission before asking for current location
        // if the permission has been granted, start getting current location and display it on the map
        if (ActivityCompat.checkSelfPermission(UserMaintenanceActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // method to get device's current location
            getCurrentLocation();

        } else { // if the permission has not been granted, prompt for permission
            ActivityCompat.requestPermissions(UserMaintenanceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        DatabaseHandler.getAllVendors(db, vendors, () -> {
            // recyclerview settings
            recyclerView = findViewById(R.id.vendor_list_recyclerview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);

            vendorListAdapter = new VendorListAdapter(UserMaintenanceActivity.this, vendors, currentLocation);
            recyclerView.setAdapter(vendorListAdapter);

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                vendorListAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                vendorListAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    /**
     * Method to handle the permission request (asking from 'else' statement from above)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if the request is as requested as above
        if (requestCode == 200) {
            // if the granted permissions array has more than 0 items, it means that the permission has been granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // method to get device's current location
                getCurrentLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        // getting last location using method from fused location client
        Task<Location> task = fusedLocationClient.getLastLocation();

        // if the task succeeds
        task.addOnSuccessListener(location -> {
            // if the last location exists
            if (location != null) {
                // Logic to handle location object
                currentLocation = location;
            }
            else {
                autoRefresh();
            }
        });
    }

    /**
     * Method to refresh maintenance activity
     */
    private void autoRefresh() {
        Intent backToHome = new Intent(this, UserMaintenanceActivity.class);
        backToHome.putExtra("loggedInUser", userTracker);
        startActivity(backToHome);
        finish();
    }
}