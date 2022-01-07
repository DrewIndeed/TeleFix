package com.example.telefixmain.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.LoginActivity;
import com.example.telefixmain.MainActivity;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.firebase.ui.auth.AuthUI;
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
    TextView signOut;

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

    // global User container
    User userTracker;

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
        cpd = new CustomProgressDialog(Objects.requireNonNull(fragmentActivity), R.style.SheetDialog);

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
            // get user from database and fill in text inputs
            DatabaseHandler.getSingleUser(
                    db,
                    fragmentActivity,
                    mUser.getUid(),
                    userResult, () -> {
                        // render on ui
                        if (userResult.size() > 0) {
                            // log to keep track
                            System.out.println(userResult.toString());

                            // set global User
                            userTracker = userResult.get(0);

                            // render user name on UI
                            profileEmail.setText(userTracker.getEmail());
                            profileName.setText(userTracker.getName());
                            profilePhone.setText(userTracker.getPhone());
                        }
                    }
            );

            // log out
            signOut = root.findViewById(R.id.tv_sign_out);
            signOut.setOnClickListener(view -> AuthUI.getInstance()
                    .signOut(fragmentActivity)
                    .addOnCompleteListener(task -> {
                        // dialog to show signing out
                        cpd.changeText("Signing out ...");
                        cpd.show();

                        new Handler().postDelayed(() -> {
                            // dismiss dialog
                            cpd.dismiss();

                            // jump to log in activity
                            new Handler().postDelayed(() -> {
                                // user is now signed out
                                Toast.makeText(fragmentActivity, "Singed out successfully!",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(fragmentActivity, LoginActivity.class));
                                fragmentActivity.finish();
                            }, 500);
                        }, 1000);
                    }));
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
                if (profileEmail.getText().toString().equals(userTracker.getEmail()))
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
                if (profileName.getText().toString().equals(userTracker.getName()))
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
                if (profilePhone.getText().toString().equals(userTracker.getPhone()))
                    updateProfileBtn.setVisibility(View.GONE);
            }
        });
        // update user info button listener
        updateProfileBtn.setOnClickListener(view -> {
            // progress dialog
            cpd.changeText("Updating user's information ...");
            cpd.show();

            // update user info on database
            DatabaseHandler.updateUser(
                    db,
                    fragmentActivity,
                    userTracker.getId().trim(),
                    profileName.getText().toString().trim(),
                    profilePhone.getText().toString().trim(),
                    profileEmail.getText().toString().trim(),
                    userTracker.isVendor(),
                    userTracker.getVendorId().trim(),
                    userTracker.getRegisteredVehicles(),
                    () -> {
                        // update user email on Firebase Auth
                        mUser.updateEmail(profileEmail.getText().toString().trim())
                                .addOnCompleteListener(task -> new Handler().postDelayed(() -> {
                                    if (task.isSuccessful()) {
                                        // dismiss dialog
                                        cpd.dismiss();

                                        // show msg
                                        Toast.makeText(fragmentActivity,
                                                "Updated user's info successfully!",
                                                Toast.LENGTH_SHORT).show();

                                        new Handler().postDelayed(() -> {
                                            // progress dialog to refresh Main Activity
                                            cpd.changeText("Refreshing ... ");
                                            cpd.show();

                                            new Handler().postDelayed(() -> {
                                                // dismiss dialog
                                                cpd.dismiss();

                                                // refresh Main Activity
                                                startActivity(new Intent(fragmentActivity,
                                                        MainActivity.class));
                                                fragmentActivity.finish();
                                            }, 1000);
                                        }, 1000);
                                    }

                                }, 1000));
                    }
            );
        });

        // show change pwd change dialog
        changePwdBtn = root.findViewById(R.id.btn_change_pwd);
        changePwdBtn.setOnClickListener(view -> {
            // show bottom sheet dialog to change password
            View changePwdDialog = openPwdChangeBottomSheetDialog(
                    R.layout.bottom_dialog_change_password, R.id.pwd_change_close_icon);

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
                                "New password's minimum length: 6",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // call verifying log in method
                        signInFromPwdChangeDialog(userResult.get(0).getEmail(),
                                currentPwdInput.getText().toString(), () -> {
                                    // progress dialog
                                    cpd.changeText("Updating password ...");
                                    cpd.show();

                                    // update password on Firebase Auth
                                    mUser.updatePassword(newPwdInput.getText().toString().trim())
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    // show msg and dismiss dialog
                                                    new Handler().postDelayed(() -> {
                                                        // dismiss dialogs
                                                        cpd.dismiss();
                                                        pwdChangeBottomDialog.dismiss();

                                                        // show msg
                                                        Toast.makeText(fragmentActivity,
                                                                "Password updated!",
                                                                Toast.LENGTH_SHORT).show();
                                                    }, 1000);
                                                }
                                            });

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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                fragmentActivity, R.style.SheetDialog);
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
                                    "Verified current user!", Toast.LENGTH_SHORT).show();

                            // run callback function
                            new Handler().postDelayed(callback, 1000);
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