package com.example.telefixmain.Util;

import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MarkerHandler {
    /**
     * Method to add markers to map and generate markers array list based on amount of Vendors
     */
    public static void generateInvisibleMarkersByVendors(GoogleMap googleMap,
                                                         ArrayList<Vendor> vendorsResultContainer,
                                                         ArrayList<Marker> vendorsMarkersContainer) {
        for (Vendor vendor : vendorsResultContainer) {
            // get marker position
            LatLng LatLng = new LatLng(
                    Double.parseDouble(vendor.getLat()),
                    Double.parseDouble(vendor.getLng())
            );

            // construct a marker
            String finalTitle = vendor.getName().length() >= 30 ?
                    (vendor.getName().substring(0, 25) + " ...") :
                    (vendor.getName());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(LatLng)
                    .title(finalTitle)
//                    .visible(false)

                    // Get the ID for tracing (disable marker title later on)
                    .snippet(vendor.getId())
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.map_marker));

            // add marker to map
            Marker currentMaker = googleMap.addMarker(markerOptions);

            // add marker to array list to keep track and get info
            vendorsMarkersContainer.add(currentMaker);
        }
    }
}
