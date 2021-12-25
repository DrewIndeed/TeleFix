package com.example.telefixmain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {
    LinearLayout llSignup;
    TextView jumpToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // sign up contents fade in
        llSignup = findViewById(R.id.ll_signup);
        llSignup.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // jump tp Login
        jumpToLogin = findViewById(R.id.jump_to_login);
        jumpToLogin.setOnClickListener(v -> {
            jumpToLogin.setTextColor(getResources().getColor(R.color.teal_200));
            new Handler().postDelayed(() -> {
                jumpToLogin.setTextColor(getResources().getColor(R.color.bmw_white));
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }, 500);
        });
    }
}