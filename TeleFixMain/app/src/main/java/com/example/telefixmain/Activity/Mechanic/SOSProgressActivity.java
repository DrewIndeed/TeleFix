package com.example.telefixmain.Activity.Mechanic;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Adapter.BillingAdapter;
import com.example.telefixmain.Model.Booking.SOSBilling;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.BookingHandler;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SOSProgressActivity extends AppCompatActivity {
    // firestore & realtime db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase vendorBookings = FirebaseDatabase.getInstance();

    // array list to contain name of services
    private ArrayList<String> services = new ArrayList<>();
    private ArrayAdapter<String> servicesAdapter;

    // xml
    private RecyclerView recyclerView;
    private BillingAdapter billingAdapter;
    private TextView currentPrice;

    private AutoCompleteTextView billingItem;
    private EditText billingQuantity;
    private Button addBillingButton;
    private Button issueBillingButton;

    private Button acceptSOSRequest;

    // init pricelist
    private HashMap<String, String> inspectionPriceContainer = new HashMap<>();
    private HashMap<String, String> repairPriceContainer = new HashMap<>();

    //current billing
    private ArrayList<SOSBilling> billings = new ArrayList<>();
    private int currentTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_sos);

        getPriceList();

        String mechanicId = "mockMechanicId"; // Mock mechanicID
        String requestId = "mockRequestId"; // Mock requestID
        String vendorId = "01"; // Mock vendorID

        // get intent
//        Intent i = getIntent();
//        String requestId = (String) i.getExtras().get("currentRequestId");
//        String vendorId = (String) i.getExtras().get("currentVendorId");
//        acceptSOSRequest = findViewById(R.id.btn_accept_sos_request);

//        // listen for db reference
//        DatabaseReference openSOSRequest = vendorBookings.getReference().child(vendorId).child("sos").child("request");
//        // set ValueEventListener that delay the onDataChange
//        ValueEventListener openSOSRequestListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds : snapshot.getChildren()) {
//
//                    SOSRequest sosRequest = ds.getValue(SOSRequest.class);
//
//                    // Todo: test on single request update (scale on arraylist later
//                    if (Objects.requireNonNull(sosRequest).getMechanicId().equals("$")) {
//                        acceptSOSRequest.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//
//        openSOSRequest.addValueEventListener(openSOSRequestListener);

//        acceptSOSRequest.setOnClickListener(view -> {
//            BookingHandler.acceptSOSRequest(vendorBookings,this,vendorId,requestId,mechanicId);
//
//            // Mocking purpose
//            BookingHandler.createProgressTracking(vendorBookings,this,vendorId,requestId,System.currentTimeMillis()/1000,() -> {});
//            acceptSOSRequest.setVisibility(View.GONE);
//        });

        //--------------Billing section--------------------
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

                    calculateTotal();
                    currentPrice.setText("Total: " + String.format("%,d",currentTotal) + ",000 VND");
                    billingItem.getText().clear();
                    billingQuantity.getText().clear();
                }
            }
        });

        // push billing to database;
        issueBillingButton = findViewById(R.id.btn_issue_billing);
        issueBillingButton.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(SOSProgressActivity.this);
            builder.setTitle("Confirm upload billing");
            builder.setMessage("Do you want to upload this bill to user ?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    BookingHandler.uploadSOSBilling(vendorBookings, SOSProgressActivity.this, vendorId, requestId, billings, currentTotal,() -> {
                        System.out.println("ACTION AFTER SUCCESSFULLY RECEIVED BILLING!!!!--------------");

                        // TODO: ADD BILLING TO DATABASE & NOTIFY USER
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
        // mock button
        Button MechanicBtnArrived = findViewById(R.id.btn_mock_arrived);
        Button MechanicBtnFixed = findViewById(R.id.btn_mock_fixed);

        MechanicBtnArrived.setOnClickListener(view -> {
            BookingHandler.updateProgressFromMechanic(vendorBookings, this, vendorId, requestId, System.currentTimeMillis()/1000L, "arrived");
        });

        MechanicBtnFixed.setOnClickListener(view -> {
            BookingHandler.updateProgressFromMechanic(vendorBookings, this, vendorId, requestId, System.currentTimeMillis()/1000L, "fixed");
        });

    }

    private void getPriceList() {
        // Mock default one
        String currentVendorId = "01";

        DatabaseHandler.getVendorPriceListById(
                db,
                currentVendorId,
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