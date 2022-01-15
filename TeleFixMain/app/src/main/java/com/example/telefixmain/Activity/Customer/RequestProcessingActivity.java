package com.example.telefixmain.Activity.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Adapter.BillingListAdapter;
import com.example.telefixmain.Model.Booking.SOSBilling;
import com.example.telefixmain.Model.Booking.SOSProgress;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.BookingHandler;
import com.example.telefixmain.Util.NotificationHandler;
import com.example.telefixmain.Util.NotificationInstance;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RequestProcessingActivity extends AppCompatActivity {
    // database objects
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Realtime Database
    private final FirebaseDatabase vendorsBookings = FirebaseDatabase.getInstance();
    DatabaseReference currentProgress;
    ValueEventListener sosProgressListener;
    DatabaseReference currentBilling;
    ValueEventListener sosBillingListener;

    // keep track of currentStep
    private int currentStep = 0;

    // xml
    StepView stepView;
    LinearLayout billingLayout;
    RecyclerView recyclerView;
    private BillingListAdapter billingAdapter;
    private TextView currentPrice;
    private boolean isApproved = false;

    // billing
    ArrayList<SOSBilling> billings = new ArrayList<>();

    // intent data receivers
    User userTracker;
    ArrayList<HashMap<String, String>> vehiclesHashMapList = new ArrayList<>();

    // user result container
    ArrayList<User> acceptedMechanicResult = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_processing);

        // get data from intent sent from Login Activity
        Intent intent = getIntent();
        userTracker = (User) intent.getSerializableExtra("loggedInUser");
        vehiclesHashMapList = (ArrayList<HashMap<String, String>>)
                intent.getSerializableExtra("vehiclesHashMapList");

        // Check total price:
        currentPrice = findViewById(R.id.tv_current_total_user);

        // recyclerview settings
        recyclerView = findViewById(R.id.read_billing_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        billingAdapter = new BillingListAdapter(RequestProcessingActivity.this, billings);
        recyclerView.setAdapter(billingAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        // binding with xml
        stepView = findViewById(R.id.step_view_on_way);
        stepView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // get intent
        Intent i = getIntent();
        String requestId = (String) i.getExtras().get("currentRequestId");
        String vendorId = (String) i.getExtras().get("currentVendorId");
        String acceptedMechanicId = intent.getStringExtra("acceptedMechanicId");

        // get accepted mechanic info and display on ui
        DatabaseHandler.getSingleUser(db, acceptedMechanicId, acceptedMechanicResult, () -> {
            ((TextView) findViewById(R.id.tv_mechanic_name_at_request_processing))
                    .setText(acceptedMechanicResult.get(0).getName());
            ((TextView) findViewById(R.id.tv_mechanic_phone_at_request_processing))
                    .setText(acceptedMechanicResult.get(0).getPhone());
        });

        System.out.println("RequestID: " + requestId + " ------------------ " + "VendorID: " + vendorId);

        // add steps for step view
        List<String> steps = new ArrayList<>();
        steps.add("Mechanic Found");
        steps.add("Mechanic Arriving");
        steps.add("Inspecting Vehicle");
        steps.add("Confirm Inspection");
        steps.add("Proceed Payment");
        stepView.setSteps(steps);
        stepView.go(1, true);

        Button userBtnDraftPayment = findViewById(R.id.btn_draft_billing);
        Button userBtnAcceptBilling = findViewById(R.id.btn_accept_billing);
        Button userBtnCancelProgress = findViewById(R.id.btn_cancel_progress);

        Button userBtnConfirmProgress = findViewById(R.id.btn_confirm_progress);
        Button userBackToHome = findViewById(R.id.btn_back_home_at_request_process);


        currentBilling = vendorsBookings.getReference(vendorId).child("sos").child("billing").child(requestId).child("timestamp");
        sosBillingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!isApproved) {
                        // Send notification when MECHANIC has issued the bill
                        String content = "Mechanic has issued the inspecting bill.";
                        NotificationHandler.sendProgressTrackingNotification(RequestProcessingActivity.this, "TeleFix - SOS Request", content);
                        userBtnDraftPayment.setVisibility(View.VISIBLE);
                        userBtnDraftPayment.startAnimation(AnimationUtils.loadAnimation(
                                RequestProcessingActivity.this, R.anim.fade_in));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        currentBilling.addValueEventListener(sosBillingListener);

        currentProgress = vendorsBookings.getReference(vendorId).child("sos").child("progress").child(requestId);
        // set ValueEventListener that delay the onDataChange
        sosProgressListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SOSProgress sosProgress = snapshot.getValue(SOSProgress.class);

                if (Objects.requireNonNull(sosProgress).getStartFixingTimestamp() != 0
                        && Objects.requireNonNull(sosProgress).getConfirmBillingTime() == 0
                        && Objects.requireNonNull(sosProgress).getAbortTime() == 0) {
                    currentStep = 2;
                    stepView.go(currentStep, true);

                    // Send notification when MECHANIC arrived
                    String content = "Mechanic has arrived at your location";
                    NotificationHandler.sendProgressTrackingNotification(RequestProcessingActivity.this, "TeleFix - SOS Request", content);

                } else if (Objects.requireNonNull(sosProgress).getStartFixingTimestamp() != 0
                        && Objects.requireNonNull(sosProgress).getStartBillingTimestamp() != 0) {
                    userBtnAcceptBilling.setVisibility(View.VISIBLE);

                    // Send notification when MECHANIC has updated the final bill
                    String content = "Mechanic has finalized your request's billing!";
                    NotificationHandler.sendProgressTrackingNotification(RequestProcessingActivity.this, "TeleFix - SOS Request", content);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        currentProgress.addValueEventListener(sosProgressListener);
        billingLayout = findViewById(R.id.layout_billing_user);

        userBtnDraftPayment.setOnClickListener(view -> {
            // Set billing visible
            billingLayout.setVisibility(View.VISIBLE);
            billingLayout.startAnimation(
                    AnimationUtils.loadAnimation(RequestProcessingActivity.this, R.anim.fade_in));

            findViewById(R.id.ll_accept_cancel_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_accept_cancel_buttons).startAnimation(
                    AnimationUtils.loadAnimation(RequestProcessingActivity.this, R.anim.fade_in));

            findViewById(R.id.ll_mechanic_info_at_request_processing).setVisibility(View.GONE);
            findViewById(R.id.ll_mechanic_info_at_request_processing).startAnimation(
                    AnimationUtils.loadAnimation(RequestProcessingActivity.this, R.anim.fade_out));

            ((ConstraintLayout) findViewById(R.id.cl_request_processing)).removeView(userBtnDraftPayment);

            BookingHandler.viewSOSBilling(vendorsBookings, this, vendorId, requestId,
                    billings, currentPrice, () -> billingAdapter.notifyDataSetChanged());
            currentStep = 3;
            stepView.go(currentStep, true);
        });

        userBtnCancelProgress.setOnClickListener(view -> {
            isApproved = false;
            BookingHandler.confirmProgressFromUser(vendorsBookings, this, vendorId, requestId,
                    System.currentTimeMillis() / 1000L, "aborted");
            currentStep = 4;
            stepView.go(currentStep, true);
            stepView.done(true);

            ((ConstraintLayout) findViewById(R.id.cl_request_processing)).removeView(billingLayout);
            userBtnConfirmProgress.setVisibility(View.GONE);
            userBtnDraftPayment.setVisibility(View.GONE);
            userBtnCancelProgress.setVisibility(View.GONE);

            Toast.makeText(this, "Thank you for choosing us! See you again!", Toast.LENGTH_SHORT).show();
            findViewById(R.id.ll_mechanic_info_at_request_processing).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_mechanic_info_at_request_processing).startAnimation(
                    AnimationUtils.loadAnimation(RequestProcessingActivity.this, R.anim.fade_in));

            userBackToHome.setVisibility(View.VISIBLE);
            userBackToHome.startAnimation(
                    AnimationUtils.loadAnimation(RequestProcessingActivity.this, R.anim.fade_in));

            // back home
            userBackToHome.setOnClickListener(view1 -> {
                Intent backToHome = new Intent(RequestProcessingActivity.this, MainActivity.class);
                backToHome.putExtra("loggedInUser", userTracker);
                backToHome.putExtra("vehiclesHashMapList", vehiclesHashMapList);
                startActivity(backToHome);
                finish();
            });
        });

        userBtnAcceptBilling.setOnClickListener(view -> {
            stepView.done(true);
            // Set billing visible
            billingLayout.setVisibility(View.VISIBLE);
            BookingHandler.viewSOSBilling(vendorsBookings, this, vendorId, requestId,
                    billings, currentPrice, () -> billingAdapter.notifyDataSetChanged());

            userBtnAcceptBilling.setVisibility(View.GONE);

            Toast.makeText(this, "Thank you for choosing us! See you again!", Toast.LENGTH_SHORT).show();
            userBackToHome.setVisibility(View.VISIBLE);
            userBackToHome.startAnimation(
                    AnimationUtils.loadAnimation(RequestProcessingActivity.this, R.anim.fade_in));

            // back home
            userBackToHome.setOnClickListener(view1 -> {
                Intent backToHome = new Intent(RequestProcessingActivity.this, MainActivity.class);
                backToHome.putExtra("loggedInUser", userTracker);
                backToHome.putExtra("vehiclesHashMapList", vehiclesHashMapList);
                startActivity(backToHome);
                finish();
            });
        });

        userBtnConfirmProgress.setOnClickListener(view -> {
            isApproved = true;
            BookingHandler.confirmProgressFromUser(vendorsBookings, this,
                    vendorId, requestId, System.currentTimeMillis() / 1000L, "confirmed");
            currentStep = 4;
            stepView.go(currentStep, true);

            userBtnConfirmProgress.setVisibility(View.GONE);
            userBtnDraftPayment.setVisibility(View.GONE);
            userBtnCancelProgress.setVisibility(View.GONE);
        });
    }

    @Override
    public void onBackPressed() {
    }
}