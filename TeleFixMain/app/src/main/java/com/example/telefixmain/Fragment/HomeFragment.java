package com.example.telefixmain.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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

import com.example.telefixmain.Adapter.VehicleListAdapter;

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.Activity.Mechanic.SOSRequestActivity;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.Model.Vehicle;
import com.example.telefixmain.R;
import com.example.telefixmain.Activity.Customer.SosActivity;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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

    // custom adapter
    VehicleListAdapter vehicleListAdapter;

    // recycler view to display data
    RecyclerView vehicleListRV;

    // layout manager for recycler view
    RecyclerView.LayoutManager vehicleListLayoutManager;

    // data receivers from constructor call
    User userTracker;
    ArrayList<HashMap<String, String>> vehiclesHashMapList;

    // declare vehicles data containers
    ArrayList<String> vehiclesIdResult;
    ArrayList<Vehicle> vehiclesResult;

    public HomeFragment(User userTracker, ArrayList<HashMap<String, String>> vehiclesHashMapList) {
        this.userTracker = userTracker;
        this.vehiclesHashMapList = vehiclesHashMapList;
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init fragment activity
        fragmentActivity = getActivity();

        // init progress dialog
        cpd = new CustomProgressDialog(Objects.requireNonNull(fragmentActivity), R.style.SheetDialog);

        // root
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home,
                container, false);

        // fade in content
        homeContent = root.findViewById(R.id.ll_home_fragment);
        homeContent.setVisibility(View.VISIBLE);
        homeContent.startAnimation(AnimationUtils.loadAnimation(fragmentActivity,
                R.anim.fade_in));

        // xml binding
        userName = root.findViewById(R.id.tv_name_home);

        // if there is a logged in user
        if (userTracker != null) {
            // render user name on UI
            userName.setText(userTracker.getName());

            // if the last vehicle has been added to hash map list
            // render to recycler view
            if (vehiclesHashMapList.size() > 0) {
                vehicleListRV = root.findViewById(R.id.rv_vehicle_list);
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

                        // create new vehicle on database
                        DatabaseHandler.createVehicle(db,
                                fragmentActivity, mUser.getUid(),
                                vBrand.getText().toString(),
                                vModel.getText().toString(),
                                vYear.getText().toString(),
                                vColor.getText().toString(),
                                vNumberPlate.getText().toString(), () -> {
                                    cpd.dismiss();
                                    bottomSheetDialog.dismiss();

                                    // init vehicles data containers
                                    vehiclesIdResult = new ArrayList<>();
                                    vehiclesResult = new ArrayList<>();
                                    vehiclesHashMapList = new ArrayList<>();

                                    // progress dialog
                                    cpd.changeText("Refreshing ...");
                                    cpd.show();

                                    // get user's vehicle list
                                    DatabaseHandler.getUserVehicleList(db, fragmentActivity, mUser.getUid(),
                                            vehiclesIdResult, vehiclesResult, () -> {
                                                // do only if there is any vehicle id, otherwise cut short the process
                                                if (vehiclesResult.size() > 0) {
                                                    // populate here
                                                    for (Vehicle currentVehicle : vehiclesResult) {
                                                        // single vehicle hash map
                                                        HashMap<String, String> tempContainer = new HashMap<>();

                                                        // inject vehicle data
                                                        tempContainer.put("vehicleTitle",
                                                                currentVehicle.getVehicleBrand() + " "
                                                                        + currentVehicle.getVehicleModel() + " "
                                                                        + currentVehicle.getVehicleYear());
                                                        tempContainer.put("vehicleColor",
                                                                currentVehicle.getVehicleColor());
                                                        tempContainer.put("vehicleNumberPlate",
                                                                currentVehicle.getVehicleNumberPlate());

                                                        // add to vehicle hash map list
                                                        vehiclesHashMapList.add(tempContainer);
                                                    }
                                                }

                                                // progress dialog
                                                new Handler().postDelayed(() -> {
                                                    cpd.dismiss();
                                                    vehicleListAdapter = new VehicleListAdapter(
                                                            fragmentActivity, vehiclesHashMapList);
                                                    vehicleListRV.setAdapter(vehicleListAdapter);
                                                }, 750);
                                            });
                                });

                    }
                });
            });
        }

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

                // jump to sos activity
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(fragmentActivity, SOSRequestActivity.class));
                    if (fragmentActivity != null) {
                        fragmentActivity.finish();
                    }
                }, 500);
            }, 1500);
        });

        // Inflate the layout for this fragment
        return root;
    }
}