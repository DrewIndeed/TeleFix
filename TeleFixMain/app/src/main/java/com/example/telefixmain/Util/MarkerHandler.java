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
                                                         ArrayList<Marker> vendorsMarkersContainer,
                                                         Runnable callback) {
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
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.map_marker));

            // add marker to map
            Marker currentMaker = googleMap.addMarker(markerOptions);

            // add marker to array list to keep track and get info
            vendorsMarkersContainer.add(currentMaker);
        }

        // run callback
        callback.run();
    }

    /**
     * Method to adjust visibility of markers from container based on defined amount
     */
    public static void enableMarkersByAmount(int amountOfMarker,
                                             ArrayList<Marker> vendorsMarkersContainer) {
        // enable visible amount
        for (int i = 0; i < amountOfMarker; i++) {
            vendorsMarkersContainer.get(i).setVisible(true);
        }

        // disable invisible amount
        for (int i = amountOfMarker; i < vendorsMarkersContainer.size(); i++) {
            vendorsMarkersContainer.get(i).setVisible(false);
        }
    }

    /**
     * Method to change amount of visible markers depending on map zoom level
     */
    public static void toggleMarkersByZoomLevel(int zoomLevel,
                                                ArrayList<Marker> vendorsMarkersContainer) {
        if (zoomLevel <= 9) {
            enableMarkersByAmount(1, vendorsMarkersContainer);
        } else if (zoomLevel <= 10) {
            enableMarkersByAmount(3, vendorsMarkersContainer);
        } else if (zoomLevel <= 14) {
            switch (zoomLevel) {
                case 11:
                    enableMarkersByAmount(5, vendorsMarkersContainer);
                    break;
                case 12:
                    enableMarkersByAmount(8, vendorsMarkersContainer);
                    break;
                case 13:
                    enableMarkersByAmount(16, vendorsMarkersContainer);
                    break;
                case 14:
                    enableMarkersByAmount(20, vendorsMarkersContainer);
                    break;
                default:
                    break;
            }
        } else {
            enableMarkersByAmount(vendorsMarkersContainer.size(), vendorsMarkersContainer);
        }
    }
}
