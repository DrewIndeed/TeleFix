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
public class LoginActivity extends AppCompatActivity {
    LinearLayout llLogin;
    TextView jumpToSignup;
    EditText pwdLogin;
    boolean pwdIsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // login contents fade in
        llLogin = findViewById(R.id.ll_login);
        llLogin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // toggle password
        pwdLogin = findViewById(R.id.pwd_login);
        pwdLogin.setOnTouchListener((view, motionEvent) -> {
            final int right = 2;
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (motionEvent.getRawX() >= pwdLogin.getRight() - pwdLogin.getCompoundDrawables()[right].getBounds().width()) {
                    int selection = pwdLogin.getSelectionEnd();

                    if (pwdIsVisible) {
                        pwdLogin.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_login_pwd, 0, R.drawable.ic_pwd_visibility_off, 0);
                        pwdLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        pwdIsVisible = false;
                    } else {
                        pwdLogin.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_login_pwd, 0, R.drawable.ic_pwd_visibility, 0);
                        pwdLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        pwdIsVisible = true;
                    }

                    pwdLogin.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        // jump tp Sign Up
        jumpToSignup = findViewById(R.id.jump_to_signup);
        jumpToSignup.setOnClickListener(view -> {
            jumpToSignup.setTextColor(getResources().getColor(R.color.orange));
            new Handler().postDelayed(() -> {
                jumpToSignup.setTextColor(getResources().getColor(R.color.bmw_white));
                startActivity(new Intent(this, SignUpActivity.class));
                finish();
            }, 500);
        });
    }
}