package com.example.telefixmain;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

public class OnWayActivity extends AppCompatActivity {
    // xml
    StepView stepView;
    Button goBtn, backBtn;

    // keep track of currentStep
    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_way);

        // binding with xml
        stepView = findViewById(R.id.step_view_on_way);
        goBtn = findViewById(R.id.go_btn);
        backBtn = findViewById(R.id.back_btn);

        // add steps for step view
        List<String> steps = new ArrayList<>();
        steps.add("Request mechanic");
        steps.add("Mechanic arriving");
        steps.add("Handling request");
        steps.add("Payment");
        stepView.setSteps(steps);

        // animate going from step 1 to step 2
        new Handler().postDelayed(() -> {
            currentStep++;
            stepView.go(currentStep, true);
        }, 1000);

        // buttons on click listeners
        goBtn.setOnClickListener(view -> {
            if (currentStep < stepView.getStepCount() - 1) {
                currentStep++;
                stepView.go(currentStep, true);
            } else {
                stepView.done(true);
            }
        });

        backBtn.setOnClickListener(view -> {
            if (currentStep > 1) {
                currentStep--;
            }
            stepView.done(false);
            stepView.go(currentStep, true);
        });
    }

    @Override
    public void onBackPressed() { }
}