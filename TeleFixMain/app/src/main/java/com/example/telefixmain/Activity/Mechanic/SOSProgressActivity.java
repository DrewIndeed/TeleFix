package com.example.telefixmain.Activity.Mechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Activity.Customer.RequestProcessingActivity;
import com.example.telefixmain.Adapter.BillingAdapter;
import com.example.telefixmain.Model.Booking.SOSBilling;
import com.example.telefixmain.Model.Booking.SOSProgress;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.BookingHandler;
import com.example.telefixmain.Util.DatabaseHandler;
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
import java.util.Objects;

public class SOSProgressActivity extends AppCompatActivity {
    // firestore & realtime db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase vendorBookings = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();

    // array list to contain name of services
    private ArrayList<String> services = new ArrayList<>();
    private ArrayAdapter<String> servicesAdapter;

    // xml
    private RecyclerView recyclerView;
    private BillingAdapter billingAdapter;
    private TextView currentPrice;
    private TextView billingStatus;

    private AutoCompleteTextView billingItem;
    private EditText billingQuantity;
    private Button addBillingButton;
    private Button issueBillingButton;

    // init pricelist
    private HashMap<String, String> inspectionPriceContainer = new HashMap<>();
    private HashMap<String, String> repairPriceContainer = new HashMap<>();

    //current billing
    private ArrayList<SOSBilling> billings = new ArrayList<>();
    private int currentTotal;

    // current progress
    private DatabaseReference currentProgress;
    private ValueEventListener sosProgressListener;

    private boolean isUploaded = false;
    private boolean isAborted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_sos);

        // get intent
        Intent intent = getIntent();
        String requestId = (String) intent.getExtras().get("requestId");
        String vendorId = (String) intent.getExtras().get("vendorId");
        String customerId = (String) intent.getExtras().get("customerId");
        long startTime = (Long) intent.getExtras().get("startTime");

        getPriceList(vendorId);

        //--------------Billing section--------------------
        // check billing status
        billingStatus = findViewById(R.id.tv_billing_status);
        checkBillStatus();

        // Check total price:
        currentPrice = findViewById(R.id.tv_current_total);
        currentPrice.setText("Total: " + String.format("%,d",currentTotal) + ",000 VND");

        // recyclerview settings
        recyclerView = findViewById(R.id.issue_billing_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        billingAdapter = new BillingAdapter(SOSProgressActivity.this,billings);
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
            // TODO: Handle invalid input
            if (billingItem.getText().toString().equals("") || billingQuantity.getText().toString().equals("")) {
                Toast.makeText(this, "Please input all the service's information (both SERVICE NAME and QUANTITY)", Toast.LENGTH_SHORT).show();
            }
            else {
                if (!services.contains(billingItem.getText().toString())) {
                    Toast.makeText(this, "Please select the registered services!", Toast.LENGTH_SHORT).show();
                } else {
                    // Check existed in current billing --> Show the current quantity to avoid duplicated
                    int index = 0;
                    boolean isRemoved = false;

                    if (billings.size() == 0) {
                        billings.add(new SOSBilling(billingItem.getText().toString(), Integer.parseInt(billingQuantity.getText().toString())));
                    }
                    else {
                        for (int i = 0; i < billings.size(); i++) {
                            index = i;
                            if (billingItem.getText().toString().equals(billings.get(i).getItem())) {
                                // TODO: If mechanic want to delete an item --> Set quantity to 0 --> Delete on list
                                if (Integer.parseInt(billingQuantity.getText().toString()) == 0) {
                                    billings.remove(i);
                                    isRemoved = true;
                                    Toast.makeText(this, "Removed the existed service", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    billings.set(i, new SOSBilling(billingItem.getText().toString(), Integer.parseInt(billingQuantity.getText().toString())));
                                    Toast.makeText(this, "Update the quantity existed service", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            if (i == billings.size()-1) {
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
                    currentPrice.setText("Total: " + String.format("%,d",currentTotal) + ",000 VND");
                    billingItem.getText().clear();
                    billingQuantity.getText().clear();
                }
            }
        });

        // push billing to database (this is the 1st draft billing);
        issueBillingButton = findViewById(R.id.btn_issue_billing);
        issueBillingButton.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(SOSProgressActivity.this);
            builder.setTitle("Confirm upload billing");
            builder.setMessage("Do you want to upload this bill to user ?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    BookingHandler.uploadSOSBilling(vendorBookings, SOSProgressActivity.this, vendorId, requestId, billings, currentTotal,() -> {
                        isUploaded = true;
                        checkBillStatus();
                        // Hide push draft billing
                        issueBillingButton.setVisibility(View.GONE);
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
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
                }
                else if (Objects.requireNonNull(sosProgress).getAbortTime() != 0) {
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
            BookingHandler.updateProgressFromMechanic(vendorBookings, this, vendorId, requestId, System.currentTimeMillis()/1000L, "arrived");
            mechanicBtnArrived.setVisibility(View.GONE);
        });

        mechanicBtnFixed.setOnClickListener(view -> {
            BookingHandler.uploadSOSBilling(vendorBookings, SOSProgressActivity.this, vendorId, requestId, billings, currentTotal,() -> {
                isUploaded = true;
                checkBillStatus();
                mechanicBtnEndProgress.setVisibility(View.VISIBLE);
                BookingHandler.updateProgressFromMechanic(vendorBookings, this, vendorId, requestId, System.currentTimeMillis()/1000L, "fixed");});
        });

        //--------End progress-------

        mechanicBtnEndProgress.setOnClickListener(view -> {
            BookingHandler.confirmSOSBilling(vendorBookings, SOSProgressActivity.this, vendorId, requestId, System.currentTimeMillis()/1000L, () -> {
                // Add to events database and exit
                AlertDialog.Builder builder = new AlertDialog.Builder(SOSProgressActivity.this);
                builder.setTitle("Confirm end SOS progress");
                builder.setMessage("Please make sure the user has paid for the request properly.");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if aborted
                        if (isAborted) {
                            DatabaseHandler.createEvent(db, SOSProgressActivity.this, requestId, customerId, vendorId,
                                    mUser.getUid(),"sos","aborted", startTime,System.currentTimeMillis()/1000L,
                                    billings, currentTotal);
                        }
                        else {
                            DatabaseHandler.createEvent(db, SOSProgressActivity.this, requestId, customerId, vendorId,
                                    mUser.getUid(),"sos","success", startTime,System.currentTimeMillis()/1000L,
                                    billings, currentTotal);
                        }

                        // Return to home activity
                        Intent i = new Intent(SOSProgressActivity.this, SOSProgressActivity.class);
                        startActivity(i);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            });
        });
    }

    private void checkBillStatus() {
        if (!isUploaded) {
            billingStatus.setText("Local changes haven't been uploaded!");
        } else {
            billingStatus.setText("Current bill is up to date!");
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
                    System.out.println("Done Fetching Services////////////////---------");
                });
    }

    private void calculateTotal() {
        currentTotal = 0;
        for (SOSBilling bill: billings) {
            String currentItem = bill.getItem();
            if (inspectionPriceContainer.containsKey(bill.getItem())) {
                currentTotal += bill.getQuantity() * Integer.parseInt(Objects.requireNonNull(inspectionPriceContainer.get(currentItem)));
            } else {
                currentTotal += bill.getQuantity() * Integer.parseInt(Objects.requireNonNull(repairPriceContainer.get(currentItem)));
            }
        }
    }
}