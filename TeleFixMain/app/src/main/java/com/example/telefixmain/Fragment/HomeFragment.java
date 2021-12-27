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

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.R;
import com.example.telefixmain.SosActivity;

import java.util.Objects;

public class HomeFragment extends Fragment {
    LinearLayout homeContent, jumpToSos;
    Activity fragmentActivity;

    // progress dialog
    CustomProgressDialog cpd;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init fragment activity
        fragmentActivity = getActivity();

        // init progress dialog
        cpd = new CustomProgressDialog(Objects.requireNonNull(fragmentActivity));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // root
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home,
                container, false);

        // fade in content
        homeContent = root.findViewById(R.id.ll_home_fragment);
        homeContent.startAnimation(AnimationUtils.loadAnimation(fragmentActivity,
                R.anim.fade_in));

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