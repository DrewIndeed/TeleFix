package com.example.telefixmain.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.telefixmain.R;
import com.example.telefixmain.SosActivity;

public class HomeFragment extends Fragment {
    LinearLayout homeContent, jumpToSos;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // root
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home,
                container, false);

        // fade in content
        homeContent = root.findViewById(R.id.ll_home_fragment);
        homeContent.startAnimation(AnimationUtils.loadAnimation((Activity) getActivity(),
                R.anim.fade_in));

        // jump to sos activity
        jumpToSos = root.findViewById(R.id.ll_sos_home);
        jumpToSos.setOnClickListener(view -> startActivity(
                new Intent((Activity) getActivity(), SosActivity.class)));

        // Inflate the layout for this fragment
        return root;
    }
}