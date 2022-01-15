package com.example.telefixmain.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Activity.Customer.MainActivity;
import com.example.telefixmain.Activity.Customer.MaintenanceActivity;
import com.example.telefixmain.Adapter.MaintenanceRequestListAdapter;
import com.example.telefixmain.Adapter.SOSRequestListAdapter;
import com.example.telefixmain.Adapter.VehicleListAdapter;

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.Model.Booking.MaintenanceRequest;
import com.example.telefixmain.Model.Booking.SOSRequest;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.Model.Vehicle;
import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.R;
import com.example.telefixmain.Activity.Customer.SosActivity;
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

public class HomeFragment extends Fragment {
    LinearLayout homeContent, jumpToSos, jumpToMaintenance;
    Activity fragmentActivity;
    TextView userName;
    AppCompatButton openVehicleRegister;

    // progress dialog
    CustomProgressDialog cpd;

    // database objects
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase vendorsBookings = FirebaseDatabase.getInstance();

    // custom adapter
    VehicleListAdapter vehicleListAdapter;

    // recycler view to display data
    RecyclerView vehicleListRV;

    // layout manager for recycler view
    RecyclerView.LayoutManager vehicleListLayoutManager;

    // data receivers from constructor call
    User userTracker;
    ArrayList<HashMap<String, String>> vehiclesHashMapList;
    String vendorId, mechanicId;

    // declare vehicles data containers
    ArrayList<String> vehiclesIdResult;
    ArrayList<Vehicle> vehiclesResult;

    ArrayList<SOSRequest> sosRequests = new ArrayList<>();
    ArrayList<MaintenanceRequest> maintenanceRequests = new ArrayList<>();
    Location currentLocation = new Location("");

    // fragment root layout
    ViewGroup root;

    public HomeFragment(User userTracker, ArrayList<HashMap<String, String>> vehiclesHashMapList, ArrayList<String> vehiclesIdResult) {
        this.userTracker = userTracker;
        this.vehiclesHashMapList = vehiclesHashMapList;
        this.vehiclesIdResult = vehiclesIdResult;
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init fragment activity
        fragmentActivity = getActivity();

        // init progress dialog
        cpd = new CustomProgressDialog(Objects.requireNonNull(fragmentActivity), R.style.SheetDialog);

        // if there is a logged in user
        if (userTracker != null) {
            System.out.println("HOME LAYOUT!");
            // if user is a Customer
            if (!Boolean.parseBoolean(userTracker.getIsMechanic())) {
                System.out.println("CUSTOMER HOME LAYOUT!");
                // root
                root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

                // render user name on UI
                userName = root.findViewById(R.id.tv_name_home);
                userName.setText(userTracker.getName());

                // fade in content
                homeContent = root.findViewById(R.id.ll_home_fragment);
                homeContent.startAnimation(AnimationUtils.loadAnimation(fragmentActivity, R.anim.fade_in));

                // render to recycler view
                vehicleListRV = root.findViewById(R.id.rv_vehicle_list);
                if (vehiclesHashMapList.size() > 0) {
                    vehicleListRV.setVisibility(View.VISIBLE);
                    vehicleListRV.setHasFixedSize(true);
                    vehicleListLayoutManager = new LinearLayoutManager(fragmentActivity);
                    vehicleListRV.setLayoutManager(vehicleListLayoutManager);
                    vehicleListAdapter = new VehicleListAdapter(
                            fragmentActivity, vehiclesHashMapList);
                    vehicleListRV.setAdapter(vehicleListAdapter);
                }

                // open register vehicle dialog
                openVehicleRegister = root.findViewById(R.id.btn_register_vehicle);
                openVehicleRegister.setOnClickListener(view -> {
                    // layout inflater
                    @SuppressLint("InflateParams")
                    View viewDialog = getLayoutInflater().inflate(
                            R.layout.bottom_dialog_vehicle_register, null);

                    // construct bottom dialog
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                            fragmentActivity, R.style.SheetDialog);
                    bottomSheetDialog.setContentView(viewDialog);
                    bottomSheetDialog.show();

                    // expand bottom dialog as default state
                    BottomSheetBehavior.from((View) viewDialog.getParent())
                            .setState(BottomSheetBehavior.STATE_EXPANDED);
                    BottomSheetBehavior.from((View) viewDialog.getParent()).setDraggable(false);

                    // when the close button is clicked
                    viewDialog.findViewById(R.id.vehicle_register_close_icon)
                            .setOnClickListener(innerView -> bottomSheetDialog.dismiss());

                    // handle register input and logic
                    viewDialog.findViewById(R.id.btn_register_vehicle).setOnClickListener(innerView -> {
                        // xml bindings
                        EditText vBrand, vModel, vYear, vColor, vNumberPlate;
                        vBrand = viewDialog.findViewById(R.id.et_brand);
                        vModel = viewDialog.findViewById(R.id.et_model);
                        vYear = viewDialog.findViewById(R.id.et_year);
                        vColor = viewDialog.findViewById(R.id.et_color);
                        vNumberPlate = viewDialog.findViewById(R.id.et_number_plate);

                        if (vBrand.getText().toString().equals("")
                                || vModel.getText().toString().equals("")
                                || vYear.getText().toString().equals("")
                                || vColor.getText().toString().equals("")
                                || vNumberPlate.getText().toString().equals("")) {
                            Toast.makeText(fragmentActivity, "Please fill in all information!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // progress dialog
                            cpd.changeText("Registering vehicle ...");
                            cpd.show();

                            String vehicleId = UUID.randomUUID().toString();

                            // create new vehicle on database
                            DatabaseHandler.createVehicle(db,
                                    fragmentActivity, mUser.getUid(),
                                    vehicleId,
                                    vBrand.getText().toString(),
                                    vModel.getText().toString(),
                                    vYear.getText().toString(),
                                    vColor.getText().toString(),
                                    vNumberPlate.getText().toString(), () -> {
                                        cpd.dismiss();
                                        bottomSheetDialog.dismiss();

                                        // init vehicles data containers
                                        vehiclesResult = new ArrayList<>();

                                        // progress dialog
                                        cpd.changeText("Refreshing ...");
                                        cpd.show();

                                        ArrayList<Vehicle> tempVehicleContainer = new ArrayList<>();
                                        DatabaseHandler.getSingleVehicle(db, vehicleId, tempVehicleContainer, () -> {
                                            // single vehicle hash map
                                            HashMap<String, String> tempContainer = new HashMap<>();

                                            // inject vehicle data
                                            tempContainer.put("vehicleTitle",
                                                    tempVehicleContainer.get(0).getVehicleBrand() + " "
                                                            + tempVehicleContainer.get(0).getVehicleModel() + " "
                                                            + tempVehicleContainer.get(0).getVehicleYear());
                                            tempContainer.put("vehicleColor",
                                                    tempVehicleContainer.get(0).getVehicleColor());
                                            tempContainer.put("vehicleNumberPlate",
                                                    tempVehicleContainer.get(0).getVehicleNumberPlate());

                                            // add to vehicle hash map list
                                            vehiclesIdResult.add(vehicleId);
                                            vehiclesHashMapList.add(tempContainer);

                                            System.out.println(vehiclesIdResult);
                                            System.out.println(vehiclesHashMapList);

                                            // intent to jump to main activity
                                            Intent toMainActivity = new Intent(fragmentActivity, MainActivity.class);
                                            toMainActivity.putExtra("loggedInUser", userTracker);
                                            toMainActivity.putExtra("vehiclesHashMapList", vehiclesHashMapList);
                                            toMainActivity.putExtra("vehiclesIdList", vehiclesIdResult);
                                            startActivity(toMainActivity);
                                        });
                                    });

                        }
                    });
                });
                // jump to sos activity
                jumpToSos = root.findViewById(R.id.ll_sos_home);
                jumpToSos.setOnClickListener(view -> {
                    // show progress dialog
                    cpd.show();

                    // hide progress dialog
                    new Handler().postDelayed(() -> {
                        cpd.dismiss();

                        // jump to sos activity
                        new Handler().postDelayed(() -> {
                            Intent jumpToSos = new Intent(fragmentActivity, SosActivity.class);
                            jumpToSos.putExtra("loggedInUser", userTracker);
                            jumpToSos.putExtra("vehiclesHashMapList", vehiclesHashMapList);
                            startActivity(jumpToSos);
                            fragmentActivity.finish();
                        }, 500);
                    }, 1500);
                });


                // MOCK: JUMP TO BILLING ACTIVITY BY CLICKING MAINTENANCE
                jumpToMaintenance = root.findViewById(R.id.ll_maintain_home);
                jumpToMaintenance.setOnClickListener(view -> {
                    // show progress dialog
                    cpd.show();

                    // hide progress dialog
                    new Handler().postDelayed(() -> {
                        cpd.dismiss();

                        new Handler().postDelayed(() -> {
                            // jump to maintenance activity
                            Intent jumpToMaintenance = new Intent(fragmentActivity, MaintenanceActivity.class);
                            jumpToMaintenance.putExtra("loggedInUser", userTracker);
                            jumpToMaintenance.putExtra("vehiclesHashMapList", vehiclesHashMapList);
                            startActivity(jumpToMaintenance);
                        }, 500);

                    }, 1500);
                });
            } else {
                System.out.println("MECHANIC HOME LAYOUT!");
                // root
                root = (ViewGroup) inflater.inflate(R.layout.fragment_home_mechanic, container, false);

                // Retrieve mechanic info
                vendorId = userTracker.getVendorId();
                mechanicId = userTracker.getId();

                // render user name on UI
                userName = root.findViewById(R.id.tv_name_mechanic_home);
                userName.setText(userTracker.getName());

                // fade in content
                homeContent = root.findViewById(R.id.ll_home_fragment_mechanic);
                homeContent.startAnimation(AnimationUtils.loadAnimation(fragmentActivity, R.anim.fade_in));

                // Get vendor's location
                getVendorLocation();

                // SOS request booking
                // recyclerview settings
                // recycler view usage and display
                RecyclerView sosRecyclerView = root.findViewById(R.id.rv_sos_pending_requests);
                LinearLayoutManager sosLLM = new LinearLayoutManager(fragmentActivity);
                SOSRequestListAdapter sosRequestAdapter = new SOSRequestListAdapter(fragmentActivity,
                        currentLocation,
                        sosRequests,
                        vendorId,
                        mechanicId,
                        userTracker,
                        vehiclesHashMapList);

                sosRecyclerView.setLayoutManager(sosLLM);
                sosRecyclerView.setAdapter(sosRequestAdapter);

                // listen for db reference
                DatabaseReference openSOSRequest = vendorsBookings.getReference()
                        .child(vendorId).child("sos").child("request");
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

                        }
                        Collections.sort(tmp, new SOSTimeStampComparator());
                        sosRequests.addAll(tmp);

                        if (sosRequests.size() > 0) {
                            // hide empty msg
                            root.findViewById(R.id.cv_no_sos_request).setVisibility(View.GONE);
                            sosRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            root.findViewById(R.id.cv_no_sos_request).setVisibility(View.VISIBLE);
                            sosRecyclerView.setVisibility(View.GONE);
                        }
                        sosRequestAdapter.notifyDataSetChanged();

//                        if (sosRequests.size() > 0) {
//                            // Push notification
//                            String content = "SOS Requests has been updated!";
//                            NotificationHandler.sendProgressTrackingNotification(fragmentActivity, "TeleFix - SOS Request", content);
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                openSOSRequest.addValueEventListener(openSOSRequestListener);


                // Maintenance request booking
                // recycler view usage and display
                RecyclerView maintenanceRecyclerView = root.findViewById(R.id.rv_maintenance_pending_requests);
                LinearLayoutManager maintenanceLLM = new LinearLayoutManager(fragmentActivity);
                MaintenanceRequestListAdapter maintenanceRequestListAdapter = new MaintenanceRequestListAdapter(fragmentActivity,
                        maintenanceRequests);

                maintenanceRecyclerView.setLayoutManager(maintenanceLLM);
                maintenanceRecyclerView.setAdapter(maintenanceRequestListAdapter);

                // listen for db reference
                DatabaseReference openMaintenanceRequests = vendorsBookings.getReference()
                        .child(vendorId).child("maintenance").child("request");
                // set ValueEventListener that delay the onDataChange
                ValueEventListener openMaintenanceRequestsListener = new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear current request list & add again
                        // Toast.makeText(fragmentActivity, "DATA CHANGE DETECTED", Toast.LENGTH_SHORT).show();
                        maintenanceRequests.clear();
                        ArrayList<MaintenanceRequest> tmp = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            MaintenanceRequest request = ds.getValue(MaintenanceRequest.class);
                            if (Objects.requireNonNull(request).getStatus().equals("on-going")) {
                                tmp.add(request);
                            }

                        }
                        // Sort collections by time created
                        Collections.sort(tmp, new MaintenanceTimeStampComparator());
                        maintenanceRequests.addAll(tmp);
                        if (maintenanceRequests.size() > 0) {
                            // hide empty msg
                            root.findViewById(R.id.cv_no_maintain_request).setVisibility(View.GONE);
                            maintenanceRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            root.findViewById(R.id.cv_no_maintain_request).setVisibility(View.VISIBLE);
                            maintenanceRecyclerView.setVisibility(View.GONE);
                        }
                        maintenanceRequestListAdapter.notifyDataSetChanged();
//                        if (maintenanceRequests.size() > 0) {
//                            // Push notification
//                            String content = "Maintenance Requests has been updated!";
//                            NotificationHandler.sendProgressTrackingNotification(fragmentActivity, "TeleFix - Maintenance Request", content);
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                openMaintenanceRequests.addValueEventListener(openMaintenanceRequestsListener);

            }
        }

        // Inflate the layout for this fragment
        return root;
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