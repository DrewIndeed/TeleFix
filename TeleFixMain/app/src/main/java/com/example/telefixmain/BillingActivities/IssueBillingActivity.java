package com.example.telefixmain.BillingActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

public class IssueBillingActivity extends AppCompatActivity {
    // firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase vendorBookings = FirebaseDatabase.getInstance();

    // array list to contain name of services
    private ArrayList<String> services = new ArrayList<>();
    private ArrayAdapter<String> servicesAdapter;

    // xml
    private RecyclerView recyclerView;
    private BillingAdapter billingAdapter;


    private AutoCompleteTextView billingItem;
    private EditText billingQuantity;
    private Button addBillingButton;
    private Button issueBillingButton;

    //current billing
    private ArrayList<SOSBilling> billings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_billing);

        getPriceList();

        // get intent
//        Intent i = getIntent();
//        String requestId = (String) i.getExtras().get("currentRequestId");
//        String vendorId = (String) i.getExtras().get("currentVendorId");

        String requestId = "abc"; // Mock requestID
        String vendorId = "01"; // Mock vendorID

        // recycleview settings
        recyclerView = findViewById(R.id.issue_billing_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        billingAdapter = new BillingAdapter(IssueBillingActivity.this,billings);
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
                    boolean isUpdate = false;
                    int index = 0;

                    if (billings.size() == 0) {
                        billings.add(new SOSBilling(billingItem.getText().toString(), Integer.parseInt(billingQuantity.getText().toString())));
                    }
                    else {
                        for (int i = 0; i < billings.size(); i++) {
                            System.out.println("Current index: " + i);
                            index = i;
                            if (billingItem.getText().toString().equals(billings.get(i).getItem())) {
                                System.out.println(billingItem.getText().toString());
                                billings.set(i, new SOSBilling(billingItem.getText().toString(), Integer.parseInt(billingQuantity.getText().toString())));
                                Toast.makeText(this, "Update the quantity existed service", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            if (i == billings.size()-1) {
                                System.out.println("Adding new item");
                                billings.add(new SOSBilling(billingItem.getText().toString(), Integer.parseInt(billingQuantity.getText().toString())));
                            }
                        }
                    }
                    billingAdapter.notifyItemChanged(index);
                    billingItem.getText().clear();
                    billingQuantity.getText().clear();
                }
            }
        });

        // push billing to database;
        issueBillingButton = findViewById(R.id.btn_issue_billing);
        issueBillingButton.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(IssueBillingActivity.this);
            builder.setTitle("Confirm upload billing");
            builder.setMessage("Do you want to upload this bill to user ?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    BookingHandler.uploadSOSBilling(vendorBookings, IssueBillingActivity.this, vendorId, requestId, billings, () -> {
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


    }

    private void getPriceList() {
        // init pricelist
        HashMap<String, String> inspectionPriceContainer = new HashMap<>();
        HashMap<String, String> repairPriceContainer = new HashMap<>();

        // Mock default one
        String currentVendorId = "01";

        DatabaseHandler.getVendorPriceListById(
                db, this,
                currentVendorId,
                inspectionPriceContainer,
                repairPriceContainer, () -> {
                    services.addAll(inspectionPriceContainer.keySet());
                    services.addAll(repairPriceContainer.keySet());
                    System.out.println("Done Fetching Services////////////////---------");
                });
    }

}