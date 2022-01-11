package com.example.telefixmain.Activity.Common;

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
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

@SuppressLint("ClickableViewAccessibility")
public class SignUpActivity extends AppCompatActivity {
    // xml element containers
    LinearLayout llSignup;
    TextView jumpToLogin;
    EditText nameSignup;
    EditText pwdSignup;
    EditText emailSignup;
    EditText phoneSignup;
    EditText vendorIdSignup;
    Button btnSignUp;

    // about password
    boolean pwdIsVisible = false;
    SwitchCompat userTypeSwitch;
    boolean isMechanic = false;

    // database objects
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // progress dialog
    CustomProgressDialog cpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // sign up contents fade in
        llSignup = findViewById(R.id.ll_signup);
        llSignup.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // init progress dialog
        cpd = new CustomProgressDialog(this, R.style.SheetDialog);

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
                isMechanic = true;
                findViewById(R.id.vendor_id_signup).setVisibility(View.VISIBLE);
                findViewById(R.id.vendor_id_signup).startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.fade_in)
                );
            } else {
                isMechanic = false;
                vendorIdSignup.getText().clear(); // Clear the current input
                vendorIdSignup.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.fade_out)
                );
                new Handler().postDelayed(() ->
                                findViewById(R.id.vendor_id_signup).setVisibility(View.GONE),
                        1000);
            }
        });

        // Parse data and signup
        nameSignup = findViewById(R.id.name_signup);
        emailSignup = findViewById(R.id.email_signup);
        phoneSignup = findViewById(R.id.phone_signup);
        vendorIdSignup = findViewById(R.id.vendor_id_signup);

        // Signup information
        btnSignUp = findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(view -> {
            // check fields if they are empty
            if ((userTypeSwitch.isChecked() && (nameSignup.getText().toString().equals("")
                    || emailSignup.getText().toString().equals("")
                    || phoneSignup.getText().toString().equals("")
                    || pwdSignup.getText().toString().equals("")
                    || vendorIdSignup.getText().toString().equals("")))
                    ||
                    (!userTypeSwitch.isChecked() && (nameSignup.getText().toString().equals("")
                            || emailSignup.getText().toString().equals("")
                            || phoneSignup.getText().toString().equals("")
                            || pwdSignup.getText().toString().equals("")))
            ) {

                Toast.makeText(this,
                        "Please fill in all information!", Toast.LENGTH_SHORT).show();
            } else {
                createAccount(emailSignup.getText().toString(), pwdSignup.getText().toString());
            }
        });

        // jump to Login Activity
        jumpToLogin = findViewById(R.id.jump_to_login);
        jumpToLogin.setOnClickListener(view -> {
            jumpToLogin.setTextColor(getResources().getColor(R.color.orange));
            new Handler().postDelayed(() -> {
                jumpToLogin.setTextColor(getResources().getColor(R.color.bmw_white));
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }, 500);
        });
    }


    /**
     * Method to get user verified from Firebase Authentication and sign up
     *
     * @param email    : string from email text input
     * @param password : string from password text input
     */
    private void createAccount(String email, String password) {
        // [START create_user_with_email]

        // show progress dialog
        cpd.changeText("Singing up ...");
        cpd.show();

        if (vendorIdSignup.getVisibility() == View.VISIBLE) {
            DatabaseHandler.isVendorExistsById(db, SignUpActivity.this,
                    vendorIdSignup.getText().toString().trim(),
                    () -> cpd.dismiss(),
                    () -> createAuthAndDatabaseUser(email, password));
        } else {
            createAuthAndDatabaseUser(email, password);
        }
    }

    /**
     * Method to create Auth and Firestore instance for User
     */
    private void createAuthAndDatabaseUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // sign up success, create user on Firestore database
                        String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        String signedUpEmail = mAuth.getCurrentUser().getEmail();

                        // Create user on database
                        DatabaseHandler.createUser(db, SignUpActivity.this,
                                id.trim(),
                                nameSignup.getText().toString().trim(),
                                phoneSignup.getText().toString().trim(),
                                Objects.requireNonNull(signedUpEmail).trim(),
                                Boolean.toString(isMechanic),
                                vendorIdSignup.getText().toString());

                        // show msg and hide progress dialog
                        cpd.dismiss();
                        Toast.makeText(this,
                                "Signed up successfully!", Toast.LENGTH_SHORT).show();

                        // jump into main activity
                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        }, 500);
                    }

                })
                .addOnFailureListener(e -> {
                    System.out.println(e.getMessage());
                    if ((Objects.requireNonNull(e.getMessage()))
                            .equals("The email address is already in use by another account.")) {
                        Toast.makeText(this,
                                "Email is already in use!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                "Signed up failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}