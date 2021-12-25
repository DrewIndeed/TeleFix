package com.example.telefixmain;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ClickableViewAccessibility")
public class SignUpActivity extends AppCompatActivity {
    LinearLayout llSignup;
    TextView jumpToLogin;
    EditText pwdSignup;
    boolean pwdIsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // sign up contents fade in
        llSignup = findViewById(R.id.ll_signup);
        llSignup.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // toggle password
        pwdSignup = findViewById(R.id.pwd_signup);
        pwdSignup.setOnTouchListener((view, motionEvent) -> {
            final int right = 2;
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (motionEvent.getRawX() >= pwdSignup.getRight() - pwdSignup.getCompoundDrawables()[right].getBounds().width()) {
                    int selection = pwdSignup.getSelectionEnd();

                    if (pwdIsVisible) {
                        pwdSignup.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_login_pwd, 0, R.drawable.ic_pwd_visibility_off, 0);
                        pwdSignup.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        pwdIsVisible = false;
                    } else {
                        pwdSignup.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_login_pwd, 0, R.drawable.ic_pwd_visibility, 0);
                        pwdSignup.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        pwdIsVisible = true;
                    }

                    pwdSignup.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        // jump tp Login
        jumpToLogin = findViewById(R.id.jump_to_login);
        jumpToLogin.setOnClickListener(v -> {
            jumpToLogin.setTextColor(getResources().getColor(R.color.orange));
            new Handler().postDelayed(() -> {
                jumpToLogin.setTextColor(getResources().getColor(R.color.bmw_white));
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }, 500);
        });
    }
}