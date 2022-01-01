package com.example.telefixmain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.telefixmain.Model.Vendor;
import com.example.telefixmain.Util.DatabaseHandler;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class SosActivity extends AppCompatActivity implements OnMapReadyCallback {

    // set the interval in which update should be received. The fastest interval indicates
    // that the application can receive the update faster when available.
    private static final long UPDATE_INTERVAL = 10000;
    private static final long FASTEST_INTERVAL = 5000;

    // map as class attribute to use in multiple methods
    private GoogleMap mMap;

    // fragment where the google map will be displayed
    private SupportMapFragment supportMapFragment;

    // using methods this class to get the last known location
    private FusedLocationProviderClient client;

    // TAG for exception handling
    private static final String TAG = SosActivity.class.getSimpleName();
    private ArrayList<Vendor> resultContainer = new ArrayList<>();

    // Current location container
    LatLng currentLocation;

    // define location of Ho Chi Minh City, Vietnam
    private final LatLng HO_CHI_MINH = new LatLng(10.8231, 106.6297);

    // firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // xml
    RelativeLayout rlSos;

    // bottom dialog tracking
    BottomSheetDialog sosBottomDialog;

    // lottie anim
    LottieAnimationView lotteAboveMsg;
    GifImageView waitGif;

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
        Button fetchBtn = findViewById(R.id.fetch_vendors);
        fetchBtn.setOnClickListener(view -> {
            DatabaseHandler.getAllVendors(db, SosActivity.this, resultContainer,
                    () -> {
                        // render on ui
                        if (resultContainer.size() > 0) {
                            // log to keep track
                            System.out.println(resultContainer.get(0).toString());
                        }});
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // initialize mMap
        mMap = googleMap;

        // start updating location by intervals
        startLocationUpdate();

        // enable the abilities to adjust zoom level
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // on map clicked listener
        mMap.setOnMapClickListener(clickedLocation -> {
            // open bottom sheet dialog
            View bottomDialogView = openBottomSheetDialog(
                    R.layout.map_bottom_sheet, R.id.sheet_close_icon);

            // get on site support
            bottomDialogView.findViewById(R.id.btn_on_site_support)
                    .setOnClickListener(view -> {
                        // dismiss dialog before open a new one to avoid window leak
                        sosBottomDialog.dismiss();

                        // waiting bottom dialog
                        View waitDialog = openBottomSheetDialog(
                                R.layout.mechanic_waiting, R.id.mechanic_wait_close_icon);

                        // animate msg
                        TextView dialogMsg = waitDialog.findViewById(R.id.mechanic_wait_msg);
                        dialogMsg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

                        // init waiting anim
                        lotteAboveMsg = waitDialog.findViewById(R.id.done_waiting_anim);
                        waitGif = waitDialog.findViewById(R.id.mechanic_wait_gif);

                        // animate when found mechanic
                        new Handler().postDelayed(() -> {
                            // hide dialog dismiss ability
                            ImageView closeDialogBtn = waitDialog.findViewById(R.id.mechanic_wait_close_icon);
                            closeDialogBtn.setEnabled(false);
                            closeDialogBtn.setVisibility(View.INVISIBLE);
                            sosBottomDialog.setCancelable(false);

                            // hide waiting gif
                            waitGif.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));

                            // lottie done anim
                            lotteAboveMsg.setVisibility(View.VISIBLE);
                            lotteAboveMsg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

                            // change msg
                            dialogMsg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
                            dialogMsg.setText("Your mechanic is on his/her way!");
                            dialogMsg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

                            // jump to mechanic arrival tracking activity
                            new Handler().postDelayed(() -> {
                                // dismiss dialog before open a new one to avoid window leak
                                sosBottomDialog.dismiss();

                                // start intent
                                startActivity(new Intent(this, RequestProcessingActivity.class));
                                finish();
                            }, 4000);
                        }, 3000);
                    });
        });
    }

    /**
     * Method to construct and show bottom sheet dialog
     */
    @SuppressLint("InflateParams")
    private View openBottomSheetDialog(int inflatedLayout, int closeIcon) {
        // layout inflater
        View viewDialog = getLayoutInflater().inflate(inflatedLayout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        sosBottomDialog = bottomSheetDialog;
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();

        // expand bottom dialog as default state
        BottomSheetBehavior.from((View) viewDialog.getParent())
                .setState(BottomSheetBehavior.STATE_EXPANDED);

        // click close icon to dismiss dialog
        viewDialog.findViewById(closeIcon)
                .setOnClickListener(view -> bottomSheetDialog.dismiss());

        return viewDialog;
    }

    /**
     * Method to handle the permission request (asking from 'else' statement from above)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

                    // add dummy marker for testing map bottom sheet
                    googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Ho Chi Minh City"));
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
                // read location of locationResult
                Location location = locationResult.getLastLocation();

                // move camera to that location
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }, null);
    }
}
