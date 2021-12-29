package com.example.telefixmain;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
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

import com.example.telefixmain.Util.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

    boolean pwdIsVisible = false;
    SwitchCompat userTypeSwitch;
    boolean isMechanic = false;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                new Handler().postDelayed(() -> {
                    findViewById(R.id.vendor_id_signup).setVisibility(View.GONE);
                }, 1000);
            }
        });

        // Parse data and signup
        nameSignup = findViewById(R.id.name_signup);
        emailSignup = findViewById(R.id.email_signup);
        phoneSignup = findViewById(R.id.phone_signup);
        vendorIdSignup = findViewById(R.id.vendor_id_signup);

        // Signup information
        btnSignUp = findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(emailSignup.getText().toString(), pwdSignup.getText().toString());

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


    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // sign up success, create user on Firestore database
                            String id = mAuth.getCurrentUser().getUid();
                            String email = mAuth.getCurrentUser().getEmail();
                            DatabaseHandler.createUserOnDatabase(db,SignUpActivity.this,
                                    id,
                                    nameSignup.getText().toString(),
                                    phoneSignup.getText().toString(),
                                    email,
                                    isMechanic,
                                    vendorIdSignup.getText().toString());
                            // jump into main activity
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();
                            }, 500);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }
}