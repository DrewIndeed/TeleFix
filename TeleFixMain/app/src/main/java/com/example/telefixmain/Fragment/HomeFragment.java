package com.example.telefixmain.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.example.telefixmain.SosActivity;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {
    LinearLayout homeContent, jumpToSos;
    Activity fragmentActivity;
    TextView userName;

    // progress dialog
    CustomProgressDialog cpd;

    // database objects
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Global Arraylist to store result
    ArrayList<User> userResult = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init fragment activity
        fragmentActivity = getActivity();

        // init progress dialog
        cpd = new CustomProgressDialog(Objects.requireNonNull(fragmentActivity));

        // root
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home,
                container, false);

        // fade in content
        homeContent = root.findViewById(R.id.ll_home_fragment);
        homeContent.setVisibility(View.VISIBLE);
        homeContent.startAnimation(AnimationUtils.loadAnimation(fragmentActivity,
                R.anim.fade_in));

        // xml binding
        userName = root.findViewById(R.id.tv_name_home);

        // if there is a logged in user
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
                            userName.setText(userResult.get(0).getName());
                            userName.startAnimation(AnimationUtils.loadAnimation(fragmentActivity,
                                    R.anim.fade_in));
                        }
                    }
            );
        }

        // jump to sos activity
        jumpToSos = root.findViewById(R.id.ll_sos_home);
        jumpToSos.setOnClickListener(view -> {
            // show progress dialog
            cpd.show();

            // hide progress dialog
            new Handler().postDelayed(() -> {
                cpd.dismiss();

                // jump to sos activity
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(fragmentActivity, SosActivity.class));
                    if (fragmentActivity != null) {
                        fragmentActivity.finish();
                    }
                }, 500);
            }, 1500);
        });

        // Inflate the layout for this fragment
        return root;
    }
}