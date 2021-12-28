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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("ClickableViewAccessibility")
public class LoginActivity extends AppCompatActivity {
    // xml element containers
    LinearLayout llLogin;
    TextView jumpToSignup;
    Button btnLogIn;
    EditText inputEmail;

    // for password
    EditText inputPwd;
    boolean pwdIsVisible = false;

    // progress dialog
    CustomProgressDialog cpd;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // init progress dialog
        cpd = new CustomProgressDialog(this);

        // login contents fade in
        llLogin = findViewById(R.id.ll_login);
        llLogin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // toggle password
        inputPwd = findViewById(R.id.pwd_login);
        inputPwd.setOnTouchListener((view, motionEvent) -> {
            final int right = 2;
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (motionEvent.getRawX() >= inputPwd.getRight() -
                        inputPwd.getCompoundDrawables()[right].getBounds().width()) {
                    int selection = inputPwd.getSelectionEnd();

                    if (pwdIsVisible) {
                        // change visibility icon
                        inputPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_login_pwd,
                                0,
                                R.drawable.ic_pwd_visibility_off,
                                0);

                        // change displayed content
                        inputPwd.setTransformationMethod(
                                PasswordTransformationMethod.getInstance()
                        );

                        // change state tracking variable
                        pwdIsVisible = false;
                    } else {
                        // change visibility icon
                        inputPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_login_pwd,
                                0,
                                R.drawable.ic_pwd_visibility,
                                0);

                        // change displayed content
                        inputPwd.setTransformationMethod(
                                HideReturnsTransformationMethod.getInstance()
                        );

                        // change state tracking variable
                        pwdIsVisible = true;
                    }

                    inputPwd.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        // signing in
        btnLogIn = findViewById(R.id.btn_login);
        inputEmail = findViewById(R.id.email_login);
        btnLogIn.setOnClickListener(view -> {
            try {
                // call verifying log on method
                signIn(inputEmail.getText().toString(), inputPwd.getText().toString());
            } catch (IllegalArgumentException | NullPointerException e) {
                // hide progress dialog
                cpd.dismiss();

                // print error
                System.out.println(e.getMessage());

                // show msg on screen
                Toast.makeText(this,
                        "Email or Password is empty", Toast.LENGTH_SHORT).show();
            }
        });

        // jump to Sign Up Activity
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

    /**
     * Method to get user verified from Firebase Authentication and log in
     *
     * @param email    : string from email text input
     * @param password : string from password text input
     */
    private void signIn(String email, String password) throws IllegalArgumentException,
            NullPointerException {
        // [START sign_in_with_email]

        // show progress dialog
        cpd.changeText("Logging in ...");
        cpd.show();

        // verifying log in info
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    System.out.println("SUCCESS!!");
                    if (task.isSuccessful()) {
                        // show msg and hide progress dialog
                        new Handler().postDelayed(() -> {
                            cpd.dismiss();
                            Toast.makeText(this,
                                    "Logged in successfully", Toast.LENGTH_SHORT).show();

                            // jump into main activity
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            }, 500);
                        }, 1000);
                    } else {
                        // show msg and hide progress dialog
                        new Handler().postDelayed(() -> {
                            cpd.dismiss();
                            Toast.makeText(this,
                                    "Email or Password is incorrect", Toast.LENGTH_SHORT).show();
                        }, 1000);
                    }
                });
        // [END sign_in_with_email]
    }
}