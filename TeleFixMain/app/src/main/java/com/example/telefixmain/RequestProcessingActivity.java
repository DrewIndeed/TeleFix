package com.example.telefixmain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.telefixmain.Model.Booking.SOSProgress;
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

    // database reference for progress tracking
    private FirebaseDatabase vendorBookings = FirebaseDatabase.getInstance();

    // keep track of currentStep
    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_processing);

        // binding with xml
        stepView = findViewById(R.id.step_view_on_way);
        stepView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // get intent
        Intent i = getIntent();
        String requestId = (String) i.getExtras().get("currentRequestId");
        String vendorId = (String) i.getExtras().get("currentVendorId");

        System.out.println("RequestID: " + requestId + " ------------------ " + "VendorID: " + vendorId);

        // mock button
        Button MechanicBtnArrived = findViewById(R.id.btn_mock_arrived);
        Button MechanicBtnFixed = findViewById(R.id.btn_mock_fixed);
        Button UserBtnCancel = findViewById(R.id.btn_abort);
        Button UserBtnProceedPayment = findViewById(R.id.btn_accept_billing);

        // add steps for step view
        List<String> steps = new ArrayList<>();
        steps.add("Mechanic found");
        steps.add("Mechanic arriving");
        steps.add("Inspecting");
        steps.add("Proceed Payment");
        stepView.setSteps(steps);

        stepView.go(1, true);
        // animate going from step 1 to step 2
//        autoGo();
        MechanicBtnArrived.setOnClickListener(view -> {
            BookingHandler.updateProgressFromMechanic(vendorBookings, this, vendorId, requestId, System.currentTimeMillis()/1000L, "arrived");
        });

        MechanicBtnFixed.setOnClickListener(view -> {
            BookingHandler.updateProgressFromMechanic(vendorBookings, this, vendorId, requestId, System.currentTimeMillis()/1000L, "fixed");
        });

        UserBtnCancel.setOnClickListener(view -> {
            BookingHandler.updateProgressFromMechanic(vendorBookings, this, vendorId, requestId, System.currentTimeMillis()/1000L, "aborted");
        });

        DatabaseReference currentProgress = vendorBookings.getReference(vendorId).child("sos").child("progress").child(requestId);
        // set ValueEventListener that delay the onDataChange
        ValueEventListener sosProgressListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SOSProgress sosProgress = snapshot.getValue(SOSProgress.class);

                if (Objects.requireNonNull(sosProgress).getAbortedTime() == 0) {
                    if (sosProgress.getStartFixingTimestamp() != 0) {
                        currentStep = 2;
                        stepView.go(currentStep, true);
                        UserBtnCancel.setVisibility(View.VISIBLE);
                    }
                    if (sosProgress.getStartFixingTimestamp() != 0 && sosProgress.getStartBillingTimestamp() != 0) {
                        currentStep = 3;
                        stepView.go(currentStep, true);
                        UserBtnProceedPayment.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    Toast.makeText(RequestProcessingActivity.this, "Abort request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        currentProgress.addValueEventListener(sosProgressListener);
        UserBtnProceedPayment.setOnClickListener(view -> {
            stepView.done(true);

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