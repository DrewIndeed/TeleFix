package com.example.telefixmain.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.telefixmain.R;

public class ProfileFragment extends Fragment {
    LinearLayout profileContent;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // fragment activity
        Activity fragmentActivity = getActivity();

        // root
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile,
                container, false);

        // fade in content
        profileContent = root.findViewById(R.id.ll_profile);
        profileContent.setVisibility(View.VISIBLE);
        profileContent.startAnimation(AnimationUtils.loadAnimation(fragmentActivity,
                R.anim.fade_in));

        // Inflate the layout for this fragment
        return root;
    }
}