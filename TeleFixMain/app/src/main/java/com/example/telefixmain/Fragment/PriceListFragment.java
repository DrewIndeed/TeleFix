package com.example.telefixmain.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.telefixmain.Adapter.PriceListAdapter;
import com.example.telefixmain.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class PriceListFragment extends Fragment {
    Activity fragmentActivity;
    GoogleMap mMap;
    ViewGroup priceListFragmentRoot;
    BottomSheetDialog currentBottomSheetDialog;

    // array list to contain hash maps of prices information
    ArrayList<HashMap<String, String>> inspectionPricesHashMapList;
    ArrayList<HashMap<String, String>> repairPricesHashMapList;

    // custom adapter
    PriceListAdapter inspectionPricesAdapter;
    PriceListAdapter repairPricesAdapter;

    // recycler view to display data
    RecyclerView inspectionPricesRV;
    RecyclerView repairPricesRV;

    // layout manager for recycler view
    RecyclerView.LayoutManager inspectionLayoutManager;
    RecyclerView.LayoutManager repairLayoutManager;

    public PriceListFragment(BottomSheetDialog currentBottomSheetDialog, GoogleMap mMap,
                             ArrayList<HashMap<String, String>> inspectionPricesHashMapList,
                             ArrayList<HashMap<String, String>> repairPricesHashMapList) {
        // Required empty public constructor
        this.currentBottomSheetDialog = currentBottomSheetDialog;
        this.mMap = mMap;
        this.inspectionPricesHashMapList = inspectionPricesHashMapList;
        this.repairPricesHashMapList = repairPricesHashMapList;
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

        // recycler views usage
        // inspection prices
        inspectionPricesRV = priceListFragmentRoot.findViewById(R.id.rv_inspection_prices);
        inspectionPricesRV.setHasFixedSize(true);
        inspectionLayoutManager = new LinearLayoutManager(fragmentActivity);
        inspectionPricesRV.setLayoutManager(inspectionLayoutManager);
        inspectionPricesAdapter = new PriceListAdapter(fragmentActivity, inspectionPricesHashMapList);
        inspectionPricesRV.setAdapter(inspectionPricesAdapter);

        // repair prices
        repairPricesRV = priceListFragmentRoot.findViewById(R.id.rv_repair_prices);
        repairPricesRV.setHasFixedSize(true);
        repairLayoutManager = new LinearLayoutManager(fragmentActivity);
        repairPricesRV.setLayoutManager(repairLayoutManager);
        repairPricesAdapter = new PriceListAdapter(fragmentActivity, repairPricesHashMapList);
        repairPricesRV.setAdapter(repairPricesAdapter);

        // Inflate the layout for this fragment
        return priceListFragmentRoot;
    }
}