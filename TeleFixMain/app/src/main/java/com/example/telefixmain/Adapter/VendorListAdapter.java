package com.example.telefixmain.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.Activity.Customer.SosActivity;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.R;

import java.util.ArrayList;
import java.util.HashMap;

public class VendorListAdapter extends RecyclerView.Adapter<VendorItemViewHolder> implements Filterable {
    Context activityContext;
    ArrayList<Vendor> vendorsList;
    ArrayList<Vendor> vendorsListOld;
    ArrayList<HashMap<String, String>> vehiclesHashMapList;
    User userTracker;

    public VendorListAdapter(Context activityContext, ArrayList<Vendor> vendorsList,
                             User userTracker, ArrayList<HashMap<String, String>> vehiclesHashMapList) {
        this.activityContext = activityContext;
        this.vendorsList = vendorsList;
        this.vendorsListOld = vendorsList;
        this.userTracker = userTracker;
        this.vehiclesHashMapList = vehiclesHashMapList;
    }

    @NonNull
    @Override
    public VendorItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_vendor_list_item, parent, false);
        return new VendorItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorItemViewHolder holder, int position) {
        Vendor vendor = vendorsList.get(position);

        if (vendor == null) { return; }

        holder.vendorName.setText(vendor.getName());

        if (vendor.getRating().equals("")) {
            holder.vendorDistance.setText("--");
        } else {
            holder.vendorDistance.setText(vendor.getRating());
        }

        holder.layout.setOnClickListener(view -> {
            Intent i = new Intent(activityContext, SosActivity.class);
            i.putExtra("loggedInUser", userTracker);
            i.putExtra("currentVendor", vendorsList.get(position));
            i.putExtra("vehiclesHashMapList", vehiclesHashMapList);
            i.putExtra("isFromMaintenance", "true");
            i.putExtra("loggedInUser", userTracker);
            activityContext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        if (vendorsList != null) {
            return vendorsList.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    vendorsList = vendorsListOld;
                } else {
                    ArrayList<Vendor> list = new ArrayList<>();
                    for (Vendor v : vendorsListOld) {
                        if (v.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(v);
                        }
                    }
                    vendorsList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = vendorsList;

                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                vendorsList = (ArrayList<Vendor>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
