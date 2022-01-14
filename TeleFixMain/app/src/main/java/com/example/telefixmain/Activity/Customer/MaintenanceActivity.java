package com.example.telefixmain.Activity.Customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;

import com.example.telefixmain.Adapter.VendorListAdapter;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MaintenanceActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Vendor> vendors = new ArrayList<>();
    ArrayList<HashMap<String, String>> vehiclesHashMapList = new ArrayList<>();

    private RecyclerView recyclerView;
    private VendorListAdapter vendorListAdapter;

    private SearchView searchView;
    private User userTracker;

    @SuppressLint("MissingPermission")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maintenance);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Search for vendor:");
        // get data from intent sent from Login Activity
        Intent intent = getIntent();
        userTracker = (User) intent.getSerializableExtra("loggedInUser");
        vehiclesHashMapList = (ArrayList<HashMap<String, String>>)
                intent.getSerializableExtra("vehiclesHashMapList");

        DatabaseHandler.getAllVendors(db, vendors, () -> {
            // recyclerview settings
            recyclerView = findViewById(R.id.vendor_list_recyclerview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);

            vendorListAdapter = new VendorListAdapter(MaintenanceActivity.this,
                    vendors, userTracker, vehiclesHashMapList);
            recyclerView.setAdapter(vendorListAdapter);

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(
                    this, DividerItemDecoration.VERTICAL);
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

        searchView.setOnCloseListener(() -> {
            //Clear query
            searchView.setQuery("", false);
            //Collapse the action view
            searchView.onActionViewCollapsed();
            return false;
        });
        return true;
    }

//    @Override
//    public void onBackPressed() {
//    }
}