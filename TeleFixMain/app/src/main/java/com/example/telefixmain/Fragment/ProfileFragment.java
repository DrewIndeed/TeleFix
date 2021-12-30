package com.example.telefixmain.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    // xml
    LinearLayout profileContent;
    EditText profileName, profileEmail, profilePhone;
    Button updateProfileBtn, changePwdBtn;

    // progress dialog
    CustomProgressDialog cpd;

    // database objects
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Global Arraylist to store result
    ArrayList<User> userResult = new ArrayList<>();

    // bottom dialog tracking
    BottomSheetDialog pwdChangeBottomDialog;

    // fragment's activity
    Activity fragmentActivity;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // fragment activity
        fragmentActivity = getActivity();

        // init progress dialog
        cpd = new CustomProgressDialog(Objects.requireNonNull(fragmentActivity));

        // root
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile,
                container, false);

        // fade in content
        profileContent = root.findViewById(R.id.ll_profile);
        profileContent.setVisibility(View.VISIBLE);
        profileContent.startAnimation(AnimationUtils.loadAnimation(fragmentActivity,
                R.anim.fade_in));


        // if there is a logged in user
        // binding with xml
        profileEmail = root.findViewById(R.id.et_profile_email);
        profileName = root.findViewById(R.id.et_profile_name);
        profilePhone = root.findViewById(R.id.et_profile_phone);
        // auto fill edit text
        if (mUser != null) {
            DatabaseHandler.getSingleUser(
                    db,
                    fragmentActivity,
                    mUser.getUid(),
                    userResult, () -> {
                        // render on ui
                        if (userResult.size() > 0) {
                            // log to keep track
                            System.out.println(userResult.toString());

                            // render user name on UI
                            profileEmail.setText(userResult.get(0).getEmail());
                            profileName.setText(userResult.get(0).getName());
                            profilePhone.setText(userResult.get(0).getPhone());
                        }
                    }
            );
        }

        // update button related
        updateProfileBtn = root.findViewById(R.id.btn_update_profile);

        // detect on text change
        profileEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateProfileBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (profileEmail.getText().toString().equals(userResult.get(0).getEmail()))
                    updateProfileBtn.setVisibility(View.GONE);
            }
        });

        profileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateProfileBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (profileName.getText().toString().equals(userResult.get(0).getName()))
                    updateProfileBtn.setVisibility(View.GONE);
            }
        });

        profilePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateProfileBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (profilePhone.getText().toString().equals(userResult.get(0).getPhone()))
                    updateProfileBtn.setVisibility(View.GONE);
            }
        });

        // show change pwd change dialog
        changePwdBtn = root.findViewById(R.id.btn_change_pwd);
        changePwdBtn.setOnClickListener(view -> {
            // show bottom sheet dialog to change password
            View changePwdDialog = openPwdChangeBottomSheetDialog(
                    R.layout.change_password_dialog, R.id.pwd_change_close_icon);

            // password edit texts
            EditText currentPwdInput = changePwdDialog.findViewById(R.id.et_enter_current_pwd);
            EditText newPwdInput = changePwdDialog.findViewById(R.id.et_enter_new_pwd);

            // read update button click
            Button updatePwdBtn = changePwdDialog.findViewById(R.id.btn_start_update_pwd);
            updatePwdBtn.setOnClickListener(subview -> {
                try {
                    if (newPwdInput.getText().toString().equals("")
                            || newPwdInput.getText().toString().length() < 6) {
                        // show msg on screen
                        Toast.makeText(fragmentActivity,
                                "New password's length: 6",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // call verifying log in method
                        signInFromPwdChangeDialog(userResult.get(0).getEmail(),
                                currentPwdInput.getText().toString(), () -> {
                                    // show msg on screen
                                    Toast.makeText(fragmentActivity,
                                            "New password is: "
                                                    + newPwdInput.getText().toString(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                } catch (IllegalArgumentException | NullPointerException e) {
                    // hide progress dialog
                    cpd.dismiss();

                    // print error
                    System.out.println(e.getMessage());

                    // show msg on screen
                    Toast.makeText(fragmentActivity,
                            "Current password is empty!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Inflate the layout for this fragment
        return root;
    }

    /**
     * Method to construct and show bottom sheet dialog
     */
    @SuppressLint("InflateParams")
    private View openPwdChangeBottomSheetDialog(int inflatedLayout, int closeIcon) {
        // layout inflater
        View viewDialog = getLayoutInflater().inflate(inflatedLayout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fragmentActivity);
        pwdChangeBottomDialog = bottomSheetDialog;
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();

        // expand bottom dialog as default state
        BottomSheetBehavior.from((View) viewDialog.getParent())
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        // click close icon to dismiss dialog
        viewDialog.findViewById(closeIcon)
                .setOnClickListener(view -> bottomSheetDialog.dismiss());

        return viewDialog;
    }

    /**
     * Method to get user verified from Firebase Authentication and log in
     *
     * @param email    : string from email text input
     * @param password : string from password text input
     */
    private void signInFromPwdChangeDialog(String email, String password, Runnable callback)
            throws IllegalArgumentException, NullPointerException {
        // [START sign_in_with_email]

        // show progress dialog
        cpd.changeText("Verifying before update ...");
        cpd.show();

        // verifying log in info
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(fragmentActivity, task -> {
                    System.out.println("VERIFIED USER INFO!");
                    if (task.isSuccessful()) {
                        // show msg and hide progress dialog
                        new Handler().postDelayed(() -> {
                            cpd.dismiss();
                            Toast.makeText(fragmentActivity,
                                    "Correct current's password!", Toast.LENGTH_SHORT).show();

                            // run callback function
                            callback.run();
                        }, 1000);
                    } else {
                        // show msg and hide progress dialog
                        new Handler().postDelayed(() -> {
                            cpd.dismiss();
                            Toast.makeText(fragmentActivity,
                                    "Incorrect current's password!", Toast.LENGTH_SHORT).show();
                        }, 1000);
                    }
                });
        // [END sign_in_with_email]
    }
}