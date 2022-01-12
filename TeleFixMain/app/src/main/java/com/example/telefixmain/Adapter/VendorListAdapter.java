package com.example.telefixmain.Adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VendorListAdapter extends RecyclerView.Adapter<VendorItemViewHolder> implements Filterable {
    private Context activityContext;
    private ArrayList<Vendor> vendorsList;
    private ArrayList<Vendor> vendorsListOld;
    private Location customerLocation;

    public VendorListAdapter(Context activityContext, ArrayList<Vendor> vendorsList, Location customerLocation) {
        this.activityContext = activityContext;
        this.customerLocation = customerLocation;
        this.vendorsList = vendorsList;
        this.vendorsListOld = vendorsList;

        // Sort by distance away
        Collections.sort(vendorsList, new Comparator<Vendor>() {
            @Override
            public int compare(Vendor v1, Vendor v2) {
                return Double.compare((Math.round(getDistanceFromCurrentLocation(Double.parseDouble(v1.getLat()), customerLocation.getLatitude(),
                        Double.parseDouble(v1.getLng()), customerLocation.getLongitude()) * 100.0) / 100.0),
                        (Math.round(getDistanceFromCurrentLocation(Double.parseDouble(v2.getLat()), customerLocation.getLatitude(),
                                Double.parseDouble(v2.getLng()), customerLocation.getLongitude()) * 100.0) / 100.0));
            }
        });
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

        // Calculate distance from VENDOR to USER
        String distanceAwayValue = Double.toString(
                Math.round(getDistanceFromCurrentLocation(
                        Double.parseDouble(vendor.getLat()), customerLocation.getLatitude(),
                        Double.parseDouble(vendor.getLng()), customerLocation.getLongitude()) * 100.0) / 100.0);

        holder.vendorName.setText(vendor.getName());
        holder.vendorDistance.setText(distanceAwayValue + "km away");
    }

    @Override
    public int getItemCount() {
        if (vendorsList != null) {
            return vendorsList.size();
        }
        return 0;
    }

    /**
     * Method to calculate distance between 2 locations based on latitude and longitude
     */
    public static double getDistanceFromCurrentLocation(double lat1, double lat2,
                                                        double lng1, double lng2) {
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lng1 = Math.toRadians(lng1);
        lng2 = Math.toRadians(lng2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dLong = lng2 - lng1;
        double dLat = lat2 - lat1;
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dLong / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    vendorsList = vendorsListOld;
                }
                else {
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

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                vendorsList = (ArrayList<Vendor>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
