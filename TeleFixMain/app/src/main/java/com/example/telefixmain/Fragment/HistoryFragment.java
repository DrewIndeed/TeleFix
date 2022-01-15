package com.example.telefixmain.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.telefixmain.Activity.Customer.MainActivity;
import com.example.telefixmain.Activity.Customer.MaintenanceActivity;
import com.example.telefixmain.Activity.Customer.SosActivity;
import com.example.telefixmain.Adapter.HistoryListAdapter;
import com.example.telefixmain.Adapter.MaintenanceRequestListAdapter;
import com.example.telefixmain.Adapter.SOSRequestListAdapter;
import com.example.telefixmain.Adapter.VehicleListAdapter;
import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.Model.Booking.MaintenanceRequest;
import com.example.telefixmain.Model.Booking.SOSRequest;
import com.example.telefixmain.Model.EventTitle;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.Model.Vehicle;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.BookingHandler;
import com.example.telefixmain.Util.Comparator.MaintenanceTimeStampComparator;
import com.example.telefixmain.Util.Comparator.SOSTimeStampComparator;
import com.example.telefixmain.Util.DatabaseHandler;
import com.example.telefixmain.Util.NotificationHandler;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class HistoryFragment extends Fragment {
    // progress dialog
    CustomProgressDialog cpd;

    // database objects
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase vendorsBookings = FirebaseDatabase.getInstance();

    // data receivers from constructor call
    User userTracker;
    String vendorId, mechanicId, userId;

    // fragment's activity
    Activity fragmentActivity;

    ArrayList<EventTitle> eventTitles = new ArrayList<>();

    // fragment root layout
    ViewGroup root;

    public HistoryFragment(User userTracker) {
        // Required empty public constructor
        this.userTracker = userTracker;
    }

    // xml
    RecyclerView historyRecylerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init fragment activity
        fragmentActivity = getActivity();

        // init progress dialog
        cpd = new CustomProgressDialog(Objects.requireNonNull(fragmentActivity), R.style.SheetDialog);

        // if there is a logged in user
        if (userTracker != null) {
            System.out.println("HISTORY LAYOUT!");
            // root
            root = (ViewGroup) inflater.inflate(R.layout.fragment_history, container, false);
            historyRecylerView = root.findViewById(R.id.rv_history_events);
            // recycler view usage and display
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentActivity);

            // if user is a Customer
            if (!Boolean.parseBoolean(userTracker.getIsMechanic())) {
                System.out.println("CUSTOMER HISTORY LAYOUT!");

                DatabaseHandler.getEvents(db, userTracker.getId(), eventTitles, () -> {
                    // Fetch assigned request
                    HistoryListAdapter historyListAdapter = new HistoryListAdapter(fragmentActivity, eventTitles ,userTracker);

                    historyRecylerView.setLayoutManager(linearLayoutManager);
                    historyRecylerView.setAdapter(historyListAdapter);
                });
            }
            else {
                System.out.println("MECHANIC HISTORY LAYOUT!");

                // Fetch assigned request
                BookingHandler.getAssignedMaintenanceRequest(vendorsBookings, fragmentActivity, userTracker.getVendorId(),userTracker.getId(), eventTitles, () -> {
                    HistoryListAdapter historyListAdapter = new HistoryListAdapter(fragmentActivity, eventTitles ,userTracker);

                    historyRecylerView.setLayoutManager(linearLayoutManager);
                    historyRecylerView.setAdapter(historyListAdapter);
                });
            }
        }
        // Inflate the layout for this fragment
        return root;
    }
}