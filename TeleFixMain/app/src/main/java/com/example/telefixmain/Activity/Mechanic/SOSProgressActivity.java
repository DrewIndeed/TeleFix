package com.example.telefixmain.Activity.Mechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Activity.Customer.MainActivity;
import com.example.telefixmain.Activity.Customer.RequestProcessingActivity;
import com.example.telefixmain.Adapter.BillingListAdapter;
import com.example.telefixmain.Adapter.SOSRequestListAdapter;
import com.example.telefixmain.Model.Booking.SOSBilling;
import com.example.telefixmain.Model.Booking.SOSProgress;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.BookingHandler;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SOSProgressActivity extends AppCompatActivity {
    // Firestore & realtime db
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase vendorBookings = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    // array list to contain name of services
    ArrayList<String> services = new ArrayList<>();
    ArrayAdapter<String> servicesAdapter;

    // xml
    RecyclerView recyclerView;
    BillingListAdapter billingAdapter;
    TextView currentPrice;
    TextView billingStatus;

    AutoCompleteTextView billingItem;
    EditText billingQuantity;
    Button addBillingButton;
    Button issueBillingButton;

    // init price list
    HashMap<String, String> inspectionPriceContainer = new HashMap<>();
    HashMap<String, String> repairPriceContainer = new HashMap<>();

    //current billing
    ArrayList<SOSBilling> billings = new ArrayList<>();
    ArrayList<String> progressCompletedTime = new ArrayList<>();
    private int currentTotal;

    // current progress
    DatabaseReference currentProgress;
    ValueEventListener sosProgressListener;

    private boolean isUploaded = false;
    private boolean isAborted = false;

    // intent data receivers
    User userTracker;
    ArrayList<HashMap<String, String>> vehiclesHashMapList = new ArrayList<>();

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_sos);

        // get intent
        Intent intent = getIntent();
        // get data from intent sent from Login Activity
        userTracker = (User) intent.getSerializableExtra("loggedInUser");
        vehiclesHashMapList = (ArrayList<HashMap<String, String>>)
                intent.getSerializableExtra("vehiclesHashMapList");
        String requestId = (String) intent.getExtras().get("requestId");
        String vendorId = (String) intent.getExtras().get("vendorId");
        String customerId = (String) intent.getExtras().get("customerId");
        long startTime = (Long) intent.getExtras().get("startTime");
        double customerLat = (double) intent.getExtras().get("customerLat");
        double customerLng = (double) intent.getExtras().get("customerLng");
        String customerAddress = getAddressFromLatLng(this,
                new LatLng(customerLat, customerLng));
        ((TextView) findViewById(R.id.customer_address)).setText(customerAddress);

        getPriceList(vendorId);

        //--------------Billing section--------------------
        // check billing status
        billingStatus = findViewById(R.id.tv_billing_status);
        checkBillStatus();

        // Check total price:
        currentPrice = findViewById(R.id.tv_current_total);
        currentPrice.setText("Total: " + String.format("%,d", currentTotal) + ",000 VND");

        // recyclerview settings
        recyclerView = findViewById(R.id.issue_billing_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        billingAdapter = new BillingListAdapter(SOSProgressActivity.this, billings);
        recyclerView.setAdapter(billingAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        // add new item to billing
        billingItem = findViewById(R.id.add_billing_item);
        billingQuantity = findViewById(R.id.add_billing_quantity);
        addBillingButton = findViewById(R.id.btn_add_item_billing);

        servicesAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.layout_dropdown_item, services);
        billingItem.setAdapter(servicesAdapter);
        billingItem.setThreshold(1);

        // update current billing (local)
        addBillingButton.setOnClickListener(view -> {
            if (billingItem.getText().toString().equals("") || billingQuantity.getText().toString().equals("")) {
                Toast.makeText(this, "Please fill in all information!", Toast.LENGTH_SHORT).show();
            } else {
                if (!services.contains(billingItem.getText().toString())) {
                    Toast.makeText(this, "Please select a service!", Toast.LENGTH_SHORT).show();
                } else {
                    // Check existed in current billing --> Show the current quantity to avoid duplicated
                    int index = 0;
                    boolean isRemoved = false;

                    if (billings.size() == 0) {
                        billings.add(new SOSBilling(billingItem.getText().toString(), Integer.parseInt(billingQuantity.getText().toString())));
                    } else {
                        for (int i = 0; i < billings.size(); i++) {
                            index = i;
                            if (billingItem.getText().toString().equals(billings.get(i).getItem())) {
                                if (Integer.parseInt(billingQuantity.getText().toString()) == 0) {
                                    billings.remove(i);
                                    isRemoved = true;
                                    Toast.makeText(this, "Removed the existed service", Toast.LENGTH_SHORT).show();
                                } else {
                                    billings.set(i, new SOSBilling(billingItem.getText().toString(), Integer.parseInt(billingQuantity.getText().toString())));
                                    Toast.makeText(this, "Updated current billing", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            if (i == billings.size() - 1) {
                                billings.add(new SOSBilling(billingItem.getText().toString(), Integer.parseInt(billingQuantity.getText().toString())));
                            }
                        }
                    }
                    if (isRemoved) {
                        billingAdapter.notifyItemRemoved(index);
                    } else {
                        billingAdapter.notifyItemChanged(index);
                    }

                    // remove flag whenever local billing list was changed
                    isUploaded = false;
                    checkBillStatus();

                    calculateTotal();
                    currentPrice.setText("Total: " + String.format("%,d", currentTotal) + ",000 VND");
                    billingItem.getText().clear();
                    billingQuantity.getText().clear();
                }
            }
        });

        // push billing to database (this is the 1st draft billing);
        issueBillingButton = findViewById(R.id.btn_issue_billing);
        issueBillingButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SOSProgressActivity.this);
            builder.setTitle("Finalizing billing");
            builder.setMessage("Do you want to issue this bill to user?");
            builder.setPositiveButton("Confirm", (dialog, id) ->
                    BookingHandler.uploadSOSBilling(vendorBookings,
                            SOSProgressActivity.this, vendorId, requestId, billings,
                            currentTotal, () -> {
                                isUploaded = true;
                                checkBillStatus();
                                ((LinearLayout) findViewById(R.id.ll_sos_progress_mechanic)).removeView(issueBillingButton);
                                addBillingButton.setEnabled(false);
                            }));
            builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();

        });

        //-------------------------Update progress------------------
        Button mechanicBtnArrived = findViewById(R.id.btn_mock_arrived);
        Button mechanicBtnFixed = findViewById(R.id.btn_mock_fixed);
        Button mechanicBtnEndProgress = findViewById(R.id.btn_end_sos_progress);

        //---------------------Listen to db----------------
        currentProgress = vendorBookings.getReference(vendorId).child("sos").child("progress").child(requestId);
        // set ValueEventListener that delay the onDataChange
        sosProgressListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SOSProgress sosProgress = snapshot.getValue(SOSProgress.class);

                // if user not abort and approve (by setting confirm billing time) --> Set Fixed (final button) visible
                if (Objects.requireNonNull(sosProgress).getAbortTime() == 0 && sosProgress.getConfirmBillingTime() != 0) {
                    mechanicBtnFixed.setVisibility(View.VISIBLE);
                    addBillingButton.setEnabled(true);
                } else if (Objects.requireNonNull(sosProgress).getAbortTime() != 0) {
                    isAborted = true;
                    mechanicBtnEndProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        currentProgress.addValueEventListener(sosProgressListener);

        mechanicBtnArrived.setOnClickListener(view -> {
            BookingHandler.updateProgressFromMechanic(vendorBookings, this, vendorId, requestId, System.currentTimeMillis() / 1000L, "arrived");

            // disappearing views
            mechanicBtnArrived.startAnimation(AnimationUtils.loadAnimation(
                    this, R.anim.fade_out));
            findViewById(R.id.mechanic_wait_gif_at_request_processing).startAnimation(AnimationUtils.loadAnimation(
                    this, R.anim.fade_out));
            findViewById(R.id.customer_address).startAnimation(AnimationUtils.loadAnimation(
                    this, R.anim.fade_out));
            ((LinearLayout) findViewById(R.id.ll_sos_progress_mechanic)).removeView(mechanicBtnArrived);
            ((LinearLayout) findViewById(R.id.ll_sos_progress_mechanic)).removeView(findViewById(R.id.mechanic_wait_gif_at_request_processing));
            ((LinearLayout) findViewById(R.id.ll_sos_progress_mechanic)).removeView(findViewById(R.id.customer_address));

            // appearing view
            findViewById(R.id.ll_bill_view_at_sos_progress_mechanic).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_service_adding_at_sos_progress_mechanic).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_issue_billing).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_bill_view_at_sos_progress_mechanic).startAnimation(AnimationUtils.loadAnimation(
                    this, R.anim.fade_in));
            findViewById(R.id.ll_service_adding_at_sos_progress_mechanic).startAnimation(AnimationUtils.loadAnimation(
                    this, R.anim.fade_in));
            findViewById(R.id.btn_issue_billing).startAnimation(AnimationUtils.loadAnimation(
                    this, R.anim.fade_in));
        });

        mechanicBtnFixed.setOnClickListener(view ->
                BookingHandler.uploadSOSBilling(vendorBookings, SOSProgressActivity.this, vendorId, requestId, billings, currentTotal, () -> {
                    isUploaded = true;
                    checkBillStatus();
                    ((LinearLayout) findViewById(R.id.ll_sos_progress_mechanic)).removeView(mechanicBtnFixed);
                    ((LinearLayout) findViewById(R.id.ll_sos_progress_mechanic)).removeView(findViewById(R.id.ll_service_adding_at_sos_progress_mechanic));
                    mechanicBtnEndProgress.setVisibility(View.VISIBLE);
                    BookingHandler.updateProgressFromMechanic(vendorBookings, this, vendorId, requestId, System.currentTimeMillis() / 1000L, "fixed");
                })
        );

        //--------End progress-------

        mechanicBtnEndProgress.setOnClickListener(view ->
                BookingHandler.confirmSOSBilling(vendorBookings, SOSProgressActivity.this, vendorId, requestId, System.currentTimeMillis() / 1000L,
                        progressCompletedTime, () -> {
                            // Add to events database and exit
                            AlertDialog.Builder builder = new AlertDialog.Builder(SOSProgressActivity.this);
                            builder.setTitle("Closing SOS request");
                            builder.setMessage("Please make sure customer has paid properly.");
                            builder.setPositiveButton("Confirm", (dialog, id) -> {
                                // if aborted
                                if (isAborted) {
                                    DatabaseHandler.createEvent(db, SOSProgressActivity.this, requestId, customerId, vendorId,
                                            mUser.getUid(), "sos", "aborted", startTime, System.currentTimeMillis() / 1000L,
                                            billings, currentTotal);
                                } else {
                                    DatabaseHandler.createEvent(db, SOSProgressActivity.this, requestId, customerId, vendorId,
                                            mUser.getUid(), "sos", "success", startTime, System.currentTimeMillis() / 1000L,
                                            billings, currentTotal);
                                    Toast.makeText(this,
                                            "Transaction completed at: " +
                                                    progressCompletedTime.get(0), Toast.LENGTH_SHORT).show();
                                }

                                // Return to home activity
                                Intent i = new Intent(SOSProgressActivity.this, MainActivity.class);
                                i.putExtra("loggedInUser", userTracker);
                                i.putExtra("vehiclesHashMapList", vehiclesHashMapList);
                                startActivity(i);
                            });
                            builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
                            AlertDialog alert = builder.create();
                            alert.show();
                        }));
    }

    @SuppressLint("SetTextI18n")
    private void checkBillStatus() {
        if (!isUploaded) {
            billingStatus.setText("Bill hasn't been issued to customer");
        } else {
            billingStatus.setText("Bill is now up to date");
        }
    }

    private void getPriceList(String vendorId) {
        DatabaseHandler.getVendorPriceListById(
                db,
                vendorId,
                inspectionPriceContainer,
                repairPriceContainer, () -> {
                    services.addAll(inspectionPriceContainer.keySet());
                    services.addAll(repairPriceContainer.keySet());
                });
    }

    private void calculateTotal() {
        currentTotal = 0;
        for (SOSBilling bill : billings) {
            String currentItem = bill.getItem();
            if (inspectionPriceContainer.containsKey(bill.getItem())) {
                currentTotal += bill.getQuantity() * Integer.parseInt(Objects.requireNonNull(inspectionPriceContainer.get(currentItem)));
            } else {
                currentTotal += bill.getQuantity() * Integer.parseInt(Objects.requireNonNull(repairPriceContainer.get(currentItem)));
            }
        }
    }

    /**
     * Method to String address based on LatLng location
     */
    public static String getAddressFromLatLng(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}