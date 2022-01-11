package com.example.telefixmain.Activity.Customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;

import com.example.telefixmain.Activity.Mechanic.SOSRequestActivity;
import com.example.telefixmain.Adapter.SOSRequestListAdapter;
import com.example.telefixmain.Adapter.VendorListAdapter;
import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maintenance);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Search for vendor:");


        // get user's location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            currentLocation = location;
                        }
                    }
                });

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
}