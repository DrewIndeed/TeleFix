package com.example.telefixmain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class SignUpActivity extends AppCompatActivity {
    // xml element containers
    LinearLayout llSignup;
    TextView jumpToLogin;
    EditText pwdSignup;
    boolean pwdIsVisible = false;
    SwitchCompat userTypeSwitch;

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
                if (motionEvent.getRawX() >= pwdSignup.getRight() -
                        pwdSignup.getCompoundDrawables()[right].getBounds().width()) {
                    int selection = pwdSignup.getSelectionEnd();

                    if (pwdIsVisible) {
                        // change visibility icon
                        pwdSignup.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_login_pwd,
                                0,
                                R.drawable.ic_pwd_visibility_off,
                                0);

                        // change showing content
                        pwdSignup.setTransformationMethod(
                                PasswordTransformationMethod.getInstance()
                        );

                        // change state tracking variable
                        pwdIsVisible = false;
                    } else {
                        // change visibility icon
                        pwdSignup.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_login_pwd,
                                0,
                                R.drawable.ic_pwd_visibility,
                                0);

                        // change showing content
                        pwdSignup.setTransformationMethod(
                                HideReturnsTransformationMethod.getInstance()
                        );

                        // change state tracking variable
                        pwdIsVisible = true;
                    }

                    pwdSignup.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        // enable vendor id input based on user type
        userTypeSwitch = findViewById(R.id.user_type_sign_up_switch);
        findViewById(R.id.vendor_id_signup).setVisibility(View.GONE);
        userTypeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                findViewById(R.id.vendor_id_signup).setVisibility(View.VISIBLE);
                findViewById(R.id.vendor_id_signup).startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.fade_in)
                );
            } else {
                findViewById(R.id.vendor_id_signup).startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.fade_out)
                );
                new Handler().postDelayed(() -> {
                    findViewById(R.id.vendor_id_signup).setVisibility(View.GONE);
                }, 1000);
            }
        });

        // jump to Login Activity
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