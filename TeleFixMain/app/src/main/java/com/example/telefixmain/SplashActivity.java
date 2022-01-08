package com.example.telefixmain;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.github.ybq.android.spinkit.style.CubeGrid;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    // xml element containers
    ImageView logoImageView;
    ProgressBar loadingAnim;
    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // logo image view
        logoImageView = findViewById(R.id.logo_at_splash);
        logoImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // progress loading animation
        loadingAnim = findViewById(R.id.spin_kit);
        loadingAnim.setIndeterminateDrawable(new CubeGrid());
        loadingAnim.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // delay to change
        new Handler().postDelayed(() -> {
            // make the loading animation disappear
            loadingAnim.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            ((RelativeLayout) loadingAnim.getParent()).removeView(loadingAnim);

            // make the start button appear
            startBtn.setVisibility(View.VISIBLE);
            startBtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        }, 2000);

        // animation when start button is clicked
        startBtn = findViewById(R.id.start_btn);
        startBtn.setOnClickListener(view -> {
            // animation of moving out of screen
            logoImageView.animate().translationY(-1400).setDuration(1000);
            startBtn.animate().translationY(1400).setDuration(1000);

            new Handler().postDelayed(() -> {
                // jump to Sign up activity
                Intent jumpToLogin = new Intent(this, SignUpActivity.class);
                startActivity(jumpToLogin);
                finish();
            }, 1000);
        });
    }
}
















