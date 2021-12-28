package com.example.telefixmain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

public class RequestProcessingActivity extends AppCompatActivity {
    // xml
    StepView stepView;

    // keep track of currentStep
    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_processing);

        // binding with xml
        stepView = findViewById(R.id.step_view_on_way);
        stepView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // add steps for step view
        List<String> steps = new ArrayList<>();
        steps.add("Request mechanic");
        steps.add("Mechanic arriving");
        steps.add("Handling request");
        steps.add("Payment");
        stepView.setSteps(steps);

        // animate going from step 1 to step 2
        autoGo();
    }

    /**
     * Method to auto increasing steps of step view
     */
    private void autoGo() {
        new Handler().postDelayed(() -> {
            if (currentStep < stepView.getStepCount() - 1) {
                currentStep++;
                stepView.go(currentStep, true);
            } else {
                stepView.done(true);
                findViewById(R.id.to_payment_button).setVisibility(View.VISIBLE);
                findViewById(R.id.to_payment_button).startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.fade_in));

                // DUMMY
                findViewById(R.id.to_payment_button).setOnClickListener(view -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
                return;
            }
            autoGo();
        }, 1000);
    }

    @Override
    public void onBackPressed() {
    }
}