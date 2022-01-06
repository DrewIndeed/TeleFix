package com.example.telefixmain.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.telefixmain.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PriceListFragment extends Fragment {
    Activity fragmentActivity;
    GoogleMap mMap;
    ViewGroup priceListFragmentRoot;
    BottomSheetDialog currentBottomSheetDialog;

    public PriceListFragment(BottomSheetDialog currentBottomSheetDialog, GoogleMap mMap) {
        // Required empty public constructor
        this.currentBottomSheetDialog = currentBottomSheetDialog;
        this.mMap = mMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // init fragment activity
        fragmentActivity = getActivity();

        // root
        priceListFragmentRoot = (ViewGroup) inflater.inflate(R.layout.fragment_price_list,
                container, false);

        // when close button of fragment is clicked
        priceListFragmentRoot.findViewById(R.id.price_list_close).setOnClickListener(view -> {
            // hide fragment
            priceListFragmentRoot.findViewById(R.id.sv_price_list_fragment).setVisibility(View.GONE);

            // enable map interactions
            mMap.getUiSettings().setAllGesturesEnabled(true);

            // show vendor dialog
            currentBottomSheetDialog.show();
        });

        // Inflate the layout for this fragment
        return priceListFragmentRoot;
    }

}