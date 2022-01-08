package com.example.telefixmain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.telefixmain.Fragment.PriceListFragment;
import com.example.telefixmain.Model.Booking.SOSMetadata;
import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.Util.BookingHandler;
import com.example.telefixmain.Util.DatabaseHandler;
import com.example.telefixmain.Util.MarkerHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import pl.droidsonroids.gif.GifImageView;

public class SosActivity extends AppCompatActivity implements OnMapReadyCallback {

    // set the interval in which update should be received. The fastest interval indicates
    // that the application can receive the update faster when available.
    private static final long UPDATE_INTERVAL = 8000;
    private static final long FASTEST_INTERVAL = 3000;

    // map as class attribute to use in multiple methods
    private GoogleMap mMap;

    // fragment where the google map will be displayed
    private SupportMapFragment supportMapFragment;

    // using methods this class to get the last known location
    private FusedLocationProviderClient client;

    // Current location container
    LatLng currentLocation;

    // TAG for exception handling
    private static final String TAG = SosActivity.class.getSimpleName();

    // define location of Ho Chi Minh City, Vietnam
    private final LatLng HO_CHI_MINH = new LatLng(10.8231, 106.6297);

    // Current user
    // database objects
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser mUser = mAuth.getCurrentUser();

    // Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Realtime Database
    private final FirebaseDatabase vendorsBookings = FirebaseDatabase.getInstance();
    private DatabaseReference currentVendorRef;
    private String currentVendorId;
    private String currentRequestId;
    private ValueEventListener sosRequestListener;

    // xml
    RelativeLayout rlSos;
    Button refreshBtn;
    TextView sheetLocation, sheetRating, sheetAddress,
            sheetWebsite, sheetContact, sheetOpenCloseTime,
            sheetDistanceAway;

    // bottom dialog tracking
    BottomSheetDialog sosBottomDialog;

    // lottie anim
    LottieAnimationView lotteAboveMsg;
    GifImageView waitGif;

    // results containers
    ArrayList<Vendor> vendorsResultContainer = new ArrayList<>();
    ArrayList<Marker> vendorsMarkersContainer = new ArrayList<>();

    // array list to contain hash maps of prices information
    ArrayList<HashMap<String, String>> inspectionPricesHashMapList;
    ArrayList<HashMap<String, String>> repairPricesHashMapList;

    // pending post delay tracker
    private Handler handlerTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // binding with the layout for this activity
        setContentView(R.layout.activity_sos);

        // main content fade in
        rlSos = findViewById(R.id.rl_sos);
        rlSos.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // binding the created fragment from xml file
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        Objects.requireNonNull(supportMapFragment).getMapAsync(this);

        // create a get fused location client
        client = LocationServices.getFusedLocationProviderClient(this);

        // checking for user's permission before asking for current location
        // if the permission has been granted, start getting current location and display it on the map
        if (ActivityCompat.checkSelfPermission(SosActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // method to get device's current location
            getCurrentLocation();

        } else { // if the permission has not been granted, prompt for permission
            ActivityCompat.requestPermissions(SosActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        // Fetch vendors
        refreshBtn = findViewById(R.id.refresh_btn_at_sos);
        refreshBtn.setOnClickListener(view -> {
        });

        // back to home fragment
        findViewById(R.id.back_home_at_sos).setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // make it empty to prevent going back using the device's "Back" button
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // initialize mMap
        mMap = googleMap;

        // enable the abilities to adjust zoom level
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // generate invisible markers from fetched vendors
        DatabaseHandler.getAllVendors(db, vendorsResultContainer,
                () -> {
                    // render on ui
                    if (vendorsResultContainer.size() > 0) {
                        MarkerHandler.generateInvisibleMarkersByVendors(mMap,
                                vendorsResultContainer, vendorsMarkersContainer,
                                () -> mMap.setOnCameraIdleListener(() -> {
                                    // get current zoom level
                                    int zoomLevel = (int) mMap.getCameraPosition().zoom;

                                    // use method to toggle markers display
                                    if (vendorsMarkersContainer != null &&
                                            vendorsMarkersContainer.size() > 0) {
                                        // enable / disable markers based on zoom level
                                        MarkerHandler.toggleMarkersByZoomLevel(
                                                zoomLevel,
                                                vendorsMarkersContainer);
                                    }
                                }));
                    }
                });

        // on map clicked listener (for testing to see if connection is fine)
        mMap.setOnMapClickListener(location -> Toast.makeText(this,
                "Detected map clicked", Toast.LENGTH_SHORT).show());

        // on markers clicked listener
        mMap.setOnMarkerClickListener(clickedMarker -> {

            // get marker location info
            LatLng clickedMarkerLocation = clickedMarker.getPosition();
            double clickedMarkerLat = clickedMarkerLocation.latitude;
            double clickedMarkerLng = clickedMarkerLocation.longitude;

            View bottomDialogView = openBottomSheetDialog(
                    R.layout.bottom_dialog_vendor_details, R.id.sheet_close_icon,
                    clickedMarkerLat, clickedMarkerLng);

            // get to vendor support
            bottomDialogView.findViewById(R.id.btn_get_there).setOnClickListener(view -> {
                // create URI with current location and destination vendor latitude and longitude
                String uri = "http://maps.google.com/maps?saddr=" +
                        currentLocation.latitude + "," + currentLocation.longitude + "&daddr=" +
                        clickedMarkerLat + "," + clickedMarkerLng;

                // create intent
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");

                // start intent to jump to google map app
                startActivity(intent);
            });


            // get on site support
            bottomDialogView.findViewById(R.id.btn_on_site_support)
                    .setOnClickListener(view -> {
                        // dismiss dialog before open a new one to avoid window leak
                        sosBottomDialog.dismiss();

                        // Create request metadata
                        currentRequestId = UUID.randomUUID().toString();
                        long createdTimestamp = System.currentTimeMillis() / 1000L;

                        // waiting bottom dialog
                        View waitDialog = openBottomSheetDialog(
                                R.layout.bottom_dialog_mechanic_waiting, R.id.mechanic_wait_close_icon,
                                0.0, 0.0);
                        TextView dialogMsg = waitDialog.findViewById(R.id.mechanic_wait_msg);
                        ImageView closeDialogBtn = waitDialog.findViewById(R.id.mechanic_wait_close_icon);

                        // init waiting anim
                        lotteAboveMsg = waitDialog.findViewById(R.id.done_waiting_anim);
                        waitGif = waitDialog.findViewById(R.id.mechanic_wait_gif);

                        // Create sos booking request on Realtime Database
                        BookingHandler.sendSOSRequest(vendorsBookings, SosActivity.this,
                                currentVendorId, mUser.getUid(), currentRequestId, createdTimestamp,
                                () -> {
                                    // animate msg
                                    dialogMsg.startAnimation(AnimationUtils
                                            .loadAnimation(this, R.anim.fade_in));

                                    // Update current requested vendor
                                    currentVendorRef = vendorsBookings.getReference(currentVendorId)
                                            .child("sos").child("metadata").child(currentRequestId);

                                    // log msg
                                    System.out.println("Current Vendor DatabaseReference has been updated!");
                                });

                        // set timeout handle variable
                        final boolean[] gotResult = new boolean[1];

                        // set ValueEventListener that delay the onDataChange
                        sosRequestListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // get SOSRequest object and use the values to update the UI
                                SOSMetadata sosRequest = dataSnapshot.getValue(SOSMetadata.class);
                                // animate when found mechanic
                                // hide dialog dismiss ability
                                if (!Objects.requireNonNull(sosRequest).getMechanicId().equals("")) {
                                    gotResult[0] = true;
                                    closeDialogBtn.setEnabled(false);
                                    closeDialogBtn.setVisibility(View.INVISIBLE);
                                    sosBottomDialog.setCancelable(false);

                                    // hide waiting gif
                                    waitGif.startAnimation(AnimationUtils.loadAnimation(
                                            SosActivity.this, R.anim.fade_out));

                                    // lottie done anim
                                    lotteAboveMsg.setVisibility(View.VISIBLE);
                                    lotteAboveMsg.startAnimation(AnimationUtils.loadAnimation(
                                            SosActivity.this, R.anim.fade_in));

                                    // change msg
                                    dialogMsg.startAnimation(AnimationUtils.loadAnimation(
                                            SosActivity.this, R.anim.fade_out));
                                    dialogMsg.setText("Your mechanic is on his/her way!");
                                    dialogMsg.startAnimation(AnimationUtils.loadAnimation(
                                            SosActivity.this, R.anim.fade_in));

                                    // jump to mechanic arrival tracking activity
                                    new Handler().postDelayed(() -> {
                                        // dismiss dialog before open a new one to avoid window leak
                                        sosBottomDialog.dismiss();

                                        // initialize progress tracking
                                        long startProgressTracking = System.currentTimeMillis() / 1000L;
                                        BookingHandler.createProgressTracking(
                                                vendorsBookings,
                                                SosActivity.this,
                                                currentVendorId,
                                                currentRequestId,
                                                startProgressTracking, () -> {
                                                    // start intent
                                                    Intent i = new Intent(SosActivity.this,
                                                            RequestProcessingActivity.class);
                                                    i.putExtra("currentVendorId", currentVendorId);
                                                    i.putExtra("currentRequestId", currentRequestId);
                                                    startActivity(i);
                                                    finish();
                                                });

                                    }, 4000);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                gotResult[0] = true;
                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            }
                        };
                        System.out.println("Done setting up ValueEventListener");

                        // Add listener to vendor with timeout
                        currentVendorRef.addValueEventListener(sosRequestListener);

                        // create expired time for sos request
                        handlerTracker = new Handler();
                        handlerTracker.postDelayed(() -> {

                            System.out.println("<><><><><> IN TESTING DELAY <><><><><>");

                            if (!gotResult[0]) { //  Timeout

                                // handle real-time database request cancellation
                                if (currentVendorRef != null && currentRequestId != null) {
                                    currentVendorRef.removeEventListener(sosRequestListener);
                                    System.out.println("VENDOR ID IN DELAY: " + currentVendorId);
                                    System.out.println("REQUEST ID IN DELAY: " + currentRequestId);
                                    BookingHandler.removeSOSRequest(
                                            vendorsBookings,
                                            SosActivity.this,
                                            currentVendorId,
                                            currentRequestId);
                                    currentVendorId = null;
                                }

                                // dismiss waiting dialog
                                sosBottomDialog.dismiss();
                            }
                        }, 10000);
                    });


            return false;
        });

        // start updating location by intervals (turn off when testing)
        startLocationUpdate();
    }

    /**
     * Method to construct and show bottom sheet dialog
     */
    @SuppressLint({"InflateParams", "SetTextI18n"})
    private View openBottomSheetDialog(int inflatedLayout,
                                       int closeIcon,
                                       Double markerLat,
                                       Double markerLng) {
        // layout inflater
        View viewDialog = getLayoutInflater().inflate(inflatedLayout, null);

        // Get the vendors ID through marker title
        System.out.println("CURRENT VENDOR ID IN DIALOG METHOD:"
                + updateCurrentVendorId(markerLat, markerLng));

        // update bottom sheet with found vendor's info
        updateVendorBottomSheetInfo(viewDialog, markerLat, markerLng);

        // construct bottom dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        sosBottomDialog = bottomSheetDialog;
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();

        // expand bottom dialog as default state
        BottomSheetBehavior.from((View) viewDialog.getParent())
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        // if it is the firsts bottom dialog of the marker
        if (closeIcon != R.id.mechanic_wait_close_icon) {
            // show price list button
            HashMap<String, String> inspectionPriceContainer = new HashMap<>();
            HashMap<String, String> repairPriceContainer = new HashMap<>();
            viewDialog.findViewById(R.id.btn_view_price_list).setOnClickListener(view -> {
                // get prices of target vendor
                DatabaseHandler.getVendorPriceListById(
                        db,
                        currentVendorId,
                        inspectionPriceContainer,
                        repairPriceContainer, () -> {
                            // log to keep track of data containers
                            System.out.println(inspectionPriceContainer.toString());
                            System.out.println(repairPriceContainer.toString());

                            // inject data
                            inspectionPricesHashMapList = new ArrayList<>();
                            repairPricesHashMapList = new ArrayList<>();
                            for (String key : inspectionPriceContainer.keySet()) {
                                HashMap<String, String> tempContainer = new HashMap<>();
                                tempContainer.put("serviceName", key);

                                // handle price format
                                String formattedPrice;
                                String currentPrice = Objects.requireNonNull(inspectionPriceContainer.get(key));
                                if (Integer.parseInt(currentPrice) >= 1000) {
                                    formattedPrice = currentPrice.charAt(0) + ",000,000 VND";
                                } else {
                                    formattedPrice = currentPrice + ",000 VND";
                                }
                                tempContainer.put("servicePrice", formattedPrice);
                                inspectionPricesHashMapList.add(tempContainer);
                            }
                            for (String key : repairPriceContainer.keySet()) {
                                HashMap<String, String> tempContainer = new HashMap<>();
                                tempContainer.put("serviceName", key);

                                // handle price format
                                String formattedPrice;
                                String currentPrice = Objects.requireNonNull(repairPriceContainer.get(key));
                                if (Integer.parseInt(currentPrice) >= 1000) {
                                    formattedPrice = currentPrice.charAt(0) + ",000,000 VND";
                                } else {
                                    formattedPrice = currentPrice + ",000 VND";
                                }
                                tempContainer.put("servicePrice", formattedPrice);
                                repairPricesHashMapList.add(tempContainer);
                            }

                            // disable map interactions when price list is on
                            mMap.getUiSettings().setAllGesturesEnabled(false);

                            // disable my location button (which gives closer focus on the location)
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                            // create instance of price list fragment
                            PriceListFragment temp = new PriceListFragment(sosBottomDialog, mMap,
                                    inspectionPricesHashMapList, repairPricesHashMapList);

                            // show price list fragment
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                                    R.anim.enter_from_right,
                                    R.anim.exit_to_left,
                                    R.anim.enter_from_left,
                                    R.anim.exit_to_right
                            ).replace(R.id.rl_sos, temp).commit();
                        });
                sosBottomDialog.dismiss();
            });
        }

        // click close icon to dismiss dialog
        viewDialog.findViewById(closeIcon).setOnClickListener(view -> {
            // handle real-time database request cancellation
            if (closeIcon == R.id.mechanic_wait_close_icon) // Check whether in view or wait or mode {
                if (currentVendorRef != null && currentRequestId != null) {
                    currentVendorRef.removeEventListener(sosRequestListener);
                    System.out.println("VENDOR ID WHEN DISMISS ICON CLICKED: " + currentVendorId);
                    System.out.println("REQUEST ID WHEN DISMISS ICON CLICKED: " + currentRequestId);
                    BookingHandler.removeSOSRequest(
                            vendorsBookings,
                            SosActivity.this,
                            currentVendorId,
                            currentRequestId);
                    currentVendorId = null;
                }

            // remove any pending delay process
            if (handlerTracker != null) handlerTracker.removeCallbacksAndMessages(null);

            // dismiss waiting dialog
            bottomSheetDialog.dismiss();
        });

        return viewDialog;
    }

    /**
     * Method to update global current vendor id
     */
    private String updateCurrentVendorId(Double markerLat,
                                         Double markerLng) {
        for (Marker m : vendorsMarkersContainer) {
            if (markerLat == m.getPosition().latitude &&
                    markerLng == m.getPosition().longitude) {

                // update global current vendor id
                currentVendorId = vendorsResultContainer
                        .get((vendorsMarkersContainer.indexOf(m)))
                        .getId();

                // stop finding
                break;
            }
        }
        return currentVendorId;
    }

    /**
     * Method to update bottom sheet content when a vendor marker is clicked
     */
    @SuppressLint("SetTextI18n")
    private void updateVendorBottomSheetInfo(View view, Double markerLat, Double markerLng) {
        // change view inner content
        for (Vendor vd : vendorsResultContainer) {
            // compare lat lng to get the representative vendor
            if (Double.parseDouble(vd.getLat()) == markerLat &&
                    Double.parseDouble(vd.getLng()) == markerLng) {
                // log to keep track
                System.out.println("TARGET VENDOR: " + vd.toString());

                // extracting info from target vendor
                String sheetLocationInfoValue = vd.getName();
                String sheetRatingValue = vd.getRating();
                String sheetAddressValue = vd.getLocation();
                String sheetWebsiteValue = vd.getWebsite();
                String sheetContactValue = vd.getContact();
                String openTimeValue = vd.getOpenTime();
                String closeTimeValue = vd.getCloseTime();

                // binding text view for bottom sheet info updates
                sheetLocation = view.findViewById(R.id.sheet_location_info);
                sheetRating = view.findViewById(R.id.rating_value);
                sheetAddress = view.findViewById(R.id.address_content);
                sheetContact = view.findViewById(R.id.phone_content);
                sheetWebsite = view.findViewById(R.id.website_content);
                sheetOpenCloseTime = view.findViewById(R.id.open_close_content);
                sheetDistanceAway = view.findViewById(R.id.reach_site_distance);
                Picasso.get().load(vd.getImg())
                        .into((ImageView) view.findViewById(R.id.iv_vendor_image_1));

                // update sheet display info
                // always available values
                sheetLocation.setText(sheetLocationInfoValue);
                sheetAddress.setText(sheetAddressValue);
                sheetOpenCloseTime.setText("Open: " + openTimeValue + " - Close: " + closeTimeValue);
                String distanceAwayValue = Double.toString(
                        Math.round(getDistanceFromCurrentLocation(
                                markerLat, currentLocation.latitude,
                                markerLng, currentLocation.longitude) * 100.0) / 100.0);
                sheetDistanceAway.setText(distanceAwayValue + " km away");
                // might be missing values
                if (sheetRatingValue.equals("")) sheetRating.setText("_");
                else sheetRating.setText(sheetRatingValue);

                if (sheetContactValue.equals("")) sheetContact.setText("No Contact");
                else sheetContact.setText(sheetContactValue);

                if (sheetWebsiteValue.equals("")) sheetWebsite.setText("No Website");
                else sheetWebsite.setText(sheetWebsiteValue);

                // end looking
                break;
            }
        }
    }

    /**
     * Method to handle the permission request (asking from 'else' statement from above)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if the request is as requested as above
        if (requestCode == 200) {
            // if the granted permissions array has more than 0 items, it means that the permission has been granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // method to get device's current location
                getCurrentLocation();
            }
        }
    }

    /**
     * Method to get device's current location
     */
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        // getting last location using method from fused location client
        Task<Location> task = client.getLastLocation();

        // if the task succeeds
        task.addOnSuccessListener(location -> {
            // if the last location exists
            if (location != null) {
                supportMapFragment.getMapAsync(googleMap -> {
                    // get that location latitude and longitude
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    currentLocation = latLng;

                    // enable my location layer on the device
                    googleMap.setMyLocationEnabled(true);

                    // enable my location button (which gives closer focus on the location)
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                    // move camera to that location
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                });
            } else { // if the last location does not exist
                Log.d(TAG, "Current location is null. Using defaults.");
                Log.e(TAG, "Exception: %s", task.getException());
                supportMapFragment.getMapAsync(googleMap -> {
                    // Ho Chi Minh City as the default location
                    // move camera to Ho Chi Minh City
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(HO_CHI_MINH, 10));
                });
            }
        });
    }

    /**
     * Method to get device's location and update map when location is changed
     */
    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void startLocationUpdate() {
        // request to get location and the level of accuracy
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // set update interval
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // location update listener
        client.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // log to keep track
                System.out.println("LOCATION UPDATE LISTENER!");

                // read location of locationResult
                Location location = locationResult.getLastLocation();

                // move camera to that location
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }, null);
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
}
