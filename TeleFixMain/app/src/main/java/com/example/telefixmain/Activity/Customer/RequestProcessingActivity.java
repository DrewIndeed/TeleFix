package com.example.telefixmain.Activity.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Adapter.BillingAdapter;
import com.example.telefixmain.Model.Booking.SOSBilling;
import com.example.telefixmain.Model.Booking.SOSProgress;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.BookingHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestProcessingActivity extends AppCompatActivity {
    // xml
    StepView stepView;

    // Realtime Database
    private final FirebaseDatabase vendorsBookings = FirebaseDatabase.getInstance();
    private DatabaseReference currentProgress;
    private ValueEventListener sosProgressListener;
    private DatabaseReference currentBilling;
    private ValueEventListener sosBillingListener;


    // keep track of currentStep
    private int currentStep = 0;

    // xml
    private LinearLayout billingLayout;
    private RecyclerView recyclerView;
    private BillingAdapter billingAdapter;
    private TextView currentPrice;
    private boolean isApproved = false;

    // billing
    private ArrayList<SOSBilling> billings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_processing);

        // Check total price:
        currentPrice = findViewById(R.id.tv_current_total_user);

        // recyclerview settings
        recyclerView = findViewById(R.id.read_billing_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        billingAdapter = new BillingAdapter(RequestProcessingActivity.this,billings);
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

        System.out.println("RequestID: " + requestId + " ------------------ " + "VendorID: " + vendorId);

        Button userBtnDraftPayment = findViewById(R.id.btn_draft_billing);
        Button userBtnProceedPayment = findViewById(R.id.btn_accept_billing);
        Button userBtnCancelProgress = findViewById(R.id.btn_cancel_progress);
        Button userBtnAcceptProgress = findViewById(R.id.btn_confirm_progress);

        // add steps for step view
        List<String> steps = new ArrayList<>();
        steps.add("Mechanic found");
        steps.add("Mechanic arriving");
        steps.add("Inspecting");
        steps.add("Confirm Proceed Fixing");
        steps.add("Proceed Payment");
        stepView.setSteps(steps);

        stepView.go(1, true);


//        UserBtnCancel.setOnClickListener(view -> {
//            BookingHandler.updateProgressFromMechanic(vendorsBookings, this, vendorId, requestId, System.currentTimeMillis()/1000L, "aborted");
//        });
         currentBilling = vendorsBookings.getReference(vendorId).child("sos").child("billing").child(requestId);
         sosBillingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!isApproved) {
                        userBtnDraftPayment.setVisibility(View.VISIBLE);
                        userBtnCancelProgress.setVisibility(View.VISIBLE);
                        userBtnAcceptProgress.setVisibility(View.VISIBLE);
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

                if (Objects.requireNonNull(sosProgress).getStartFixingTimestamp() != 0 && Objects.requireNonNull(sosProgress).getConfirmBillingTime() == 0 && Objects.requireNonNull(sosProgress).getAbortTime() == 0) {
                    currentStep = 2;
                    stepView.go(currentStep, true);
                }
                else if (Objects.requireNonNull(sosProgress).getStartFixingTimestamp() != 0 && Objects.requireNonNull(sosProgress).getStartBillingTimestamp() != 0) {
                    userBtnProceedPayment.setVisibility(View.VISIBLE);
                }
//                else if (Objects.requireNonNull(sosProgress).getAbortTime() !=  0) {
//                    currentStep =
//                }
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
            BookingHandler.viewSOSBilling(vendorsBookings, this, vendorId, requestId, billings, currentPrice, () -> {
                billingAdapter.notifyDataSetChanged();
            });
            findViewById(R.id.processing_request_gif).setVisibility(View.GONE);
            currentStep = 3;
            stepView.go(currentStep, true);
        });

        userBtnCancelProgress.setOnClickListener(view -> {
            isApproved = false;
            BookingHandler.confirmProgressFromUser(vendorsBookings, this, vendorId, requestId, System.currentTimeMillis()/1000L, "aborted");;
            currentStep = 4;
            stepView.go(currentStep, true);
            stepView.done(true);

            billingLayout.setVisibility(View.GONE);
            findViewById(R.id.processing_request_gif).setVisibility(View.VISIBLE);
            userBtnAcceptProgress.setVisibility(View.GONE);
            userBtnDraftPayment.setVisibility(View.GONE);
            userBtnCancelProgress.setVisibility(View.GONE);

            findViewById(R.id.to_payment_button).setVisibility(View.VISIBLE);
            findViewById(R.id.to_payment_button).startAnimation(
                    AnimationUtils.loadAnimation(RequestProcessingActivity.this, R.anim.fade_in));

            // DUMMY
            findViewById(R.id.to_payment_button).setOnClickListener(view1 -> {
                startActivity(new Intent(RequestProcessingActivity.this, MainActivity.class));
                finish();
            });
        });

        userBtnAcceptProgress.setOnClickListener(view -> {
            isApproved = true;
            BookingHandler.confirmProgressFromUser(vendorsBookings, this, vendorId, requestId, System.currentTimeMillis()/1000L, "confirmed");;
            currentStep = 4;
            stepView.go(currentStep, true);

            billingLayout.setVisibility(View.GONE);
            findViewById(R.id.processing_request_gif).setVisibility(View.VISIBLE);
            userBtnAcceptProgress.setVisibility(View.GONE);
            userBtnDraftPayment.setVisibility(View.GONE);
            userBtnCancelProgress.setVisibility(View.GONE);
        });

        userBtnProceedPayment.setOnClickListener(view -> {
            stepView.done(true);
            // Set billing visible
            billingLayout.setVisibility(View.VISIBLE);
            BookingHandler.viewSOSBilling(vendorsBookings, this, vendorId, requestId, billings, currentPrice, () -> {
                billingAdapter.notifyDataSetChanged();
            });
            findViewById(R.id.processing_request_gif).setVisibility(View.GONE);
            userBtnProceedPayment.setVisibility(View.GONE);

            findViewById(R.id.to_payment_button).setVisibility(View.VISIBLE);
            findViewById(R.id.to_payment_button).startAnimation(
                    AnimationUtils.loadAnimation(RequestProcessingActivity.this, R.anim.fade_in));

            // DUMMY
            findViewById(R.id.to_payment_button).setOnClickListener(view1 -> {
                startActivity(new Intent(RequestProcessingActivity.this, MainActivity.class));
                finish();
            });
        });

    }

    @Override
    public void onBackPressed() {
    }
}