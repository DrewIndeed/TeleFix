package com.example.telefixmain.BillingActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.telefixmain.Adapter.BillingAdapter;
import com.example.telefixmain.Model.Booking.SOSBilling;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class IssueBillingActivity extends AppCompatActivity {
    // firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        addBillingButton = findViewById(R.id.btn_add_item_issue_billing);

        servicesAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.layout_dropdown_item, services);
        billingItem.setAdapter(servicesAdapter);
        billingItem.setThreshold(1);


        addBillingButton.setOnClickListener(view -> {
            System.out.println(billingItem.getText().toString() + " ----------- " + Integer.parseInt(billingQuantity.getText().toString()));
            billings.add(new SOSBilling(billingItem.getText().toString(), Integer.parseInt(billingQuantity.getText().toString())));
            billingAdapter.notifyDataSetChanged();
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