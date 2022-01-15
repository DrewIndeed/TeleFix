package com.example.telefixmain.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Adapter.SOSRequestListAdapter;
import com.example.telefixmain.Model.Booking.MaintenanceRequest;
import com.example.telefixmain.Model.Booking.Billing;
import com.example.telefixmain.Model.Booking.SOSRequest;
import com.example.telefixmain.Model.Booking.SOSProgress;
import com.example.telefixmain.Model.EventTitle;
import com.example.telefixmain.Util.Comparator.EventTitleTimeStampComparator;
import com.example.telefixmain.Util.Comparator.MaintenanceTimeStampComparator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookingHandler {
    // method for USER to send sos request to realtime db
    public static void sendSOSRequest(FirebaseDatabase rootNode,
                                      Context context,
                                      String vendorId,
                                      String userId,
                                      String requestId,
                                      long timeCreated,
                                      double currentLat,
                                      double currentLng,
                                      Runnable callback) {

        System.out.println(vendorId + " " + userId);

        // Get the root reference of chosen vendor
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        // Initialize sosRequest object
        SOSRequest sosRequest = new SOSRequest(requestId, userId, timeCreated, currentLat, currentLng);

        // Send request object to db
        vendorRef.child("sos").child("request").child(requestId).setValue(sosRequest)
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "Request sent!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
        callback.run();
    }

    // method for MECHANIC to accept the request
    public static void acceptSOSRequest(FirebaseDatabase rootNode,
                                        Context context,
                                        String vendorId,
                                        String requestId,
                                        String mechanicId,
                                        Runnable callback) {
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        // Set value to "mechanicId"
        vendorRef.child("sos").child("request").child(requestId).child("mechanicId").setValue(mechanicId)
                .addOnCompleteListener(task -> {
                    Toast.makeText(context,
                            "Request accepted by selected vendor", Toast.LENGTH_SHORT).show();
                    callback.run();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // method for USER SOS request on the db
    public static void removeSOSRequest(FirebaseDatabase rootNode,
                                        Context context,
                                        String vendorId,
                                        String requestId) {

        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        vendorRef.child("sos").child("request").child(requestId).removeValue()
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "Request cancelled!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // method for MECHANIC create SOS progress tracking
    public static void createProgressTracking(FirebaseDatabase rootNode,
                                              Context context,
                                              String vendorId,
                                              String requestId,
                                              long timeAccepted,
                                              Runnable callback) {
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        // initialize sosProgress object
        SOSProgress sosProgress = new SOSProgress(timeAccepted);

        // send init object to database
        vendorRef.child("sos").child("progress").child(requestId).setValue(sosProgress)
                .addOnCompleteListener(task -> callback.run())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // method for MECHANIC to update progress
    public static void updateProgressFromMechanic(FirebaseDatabase rootNode,
                                                  Context context,
                                                  String vendorId,
                                                  String requestId,
                                                  long timeStamp,
                                                  String type) {
        DatabaseReference progressRef = rootNode.getReference(vendorId).child("sos").child("progress").child(requestId);

        switch (type) {
            case "arrived":
                progressRef.child("startFixingTimestamp").setValue(timeStamp)
                        .addOnCompleteListener(task -> Toast.makeText(context,
                                "Mechanic has arrived. Start inspecting.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "" +
                                e.getMessage(), Toast.LENGTH_SHORT).show());
                break;
            case "fixed":
                progressRef.child("startBillingTimestamp").setValue(timeStamp)
                        .addOnCompleteListener(task -> Toast.makeText(context,
                                "Finished fixing. Start issuing bill.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "" +
                                e.getMessage(), Toast.LENGTH_SHORT).show());
                break;
        }
    }

    // method for MECHANIC to upload billing
    public static void uploadSOSBilling(FirebaseDatabase rootNode,
                                        Context context,
                                        String vendorId,
                                        String requestId,
                                        ArrayList<Billing> currentBilling,
                                        int total,
                                        Runnable callback) {
        DatabaseReference billingRef = rootNode.getReference(vendorId).child("sos").child("billing").child(requestId);

        Map<String, Integer> billingData = new HashMap<>();

        for (Billing bill :
                currentBilling) {
            billingData.put(bill.getItem(), bill.getQuantity());
        }

        billingRef.child("timestamp").setValue(System.currentTimeMillis() / 1000L);
        billingRef.child("paidTime").setValue(0);
        billingRef.child("total").setValue(total);
        billingRef.child("data").setValue(billingData)
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "Inspection bill uploaded!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());

        callback.run();
    }

    // method for USER to view current uploaded bill
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public static void viewSOSBilling(FirebaseDatabase rootNode,
                                      Context context,
                                      String vendorId,
                                      String requestId,
                                      ArrayList<Billing> currentBilling,
                                      TextView tvTotal,
                                      Runnable callback) {
        DatabaseReference billingRef = rootNode.getReference(vendorId).child("sos").child("billing").child(requestId);

        billingRef.get()
                .addOnSuccessListener(dataSnapshot -> {
                    // Get billing data
                    GenericTypeIndicator<Map<String, Integer>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Integer>>() {
                    };
                    Map<String, Integer> data = dataSnapshot.child("data").getValue(genericTypeIndicator);

                    ArrayList<Billing> tmpBillList = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : Objects.requireNonNull(data).entrySet()) {
                        Billing tmpBilling = new Billing(entry.getKey(), entry.getValue());
                        tmpBillList.add(tmpBilling);
                    }
                    currentBilling.clear();
                    currentBilling.addAll(tmpBillList);

                    callback.run();

                    // Get total
                    int currentTotal = dataSnapshot.child("total").getValue(Integer.class);
                    tvTotal.setText("Total: " + String.format("%,d", currentTotal) + ",000 VND");
                })
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // method for USER to confirm/abort progress after view 1st bill
    public static void confirmProgressFromUser(FirebaseDatabase rootNode,
                                               Context context,
                                               String vendorId,
                                               String requestId,
                                               long timeStamp,
                                               String type) {
        DatabaseReference progressRef = rootNode.getReference(vendorId).child("sos").child("progress").child(requestId);

        switch (type) {
            case "aborted":
                progressRef.child("abortTime").setValue(timeStamp)
                        .addOnCompleteListener(task -> Toast.makeText(context,
                                "Customer has refused fixing request.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "" +
                                e.getMessage(), Toast.LENGTH_SHORT).show());
                break;
            case "confirmed":
                progressRef.child("confirmBillingTime").setValue(timeStamp)
                        .addOnCompleteListener(task -> Toast.makeText(context,
                                "Billing confirmed. Mechanic starts fixing.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "" +
                                e.getMessage(), Toast.LENGTH_SHORT).show());
                break;
        }
    }

    // method for MECHANIC to confirm receiving payment
    public static void confirmSOSBilling(FirebaseDatabase rootNode,
                                         Context context,
                                         String vendorId,
                                         String requestId,
                                         long timestamp,
                                         Runnable callback) {
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        // Set value to "mechanicId"
        vendorRef.child("sos").child("billing").child(requestId).child("paidTime").setValue(timestamp)
                .addOnCompleteListener(task -> {
                    Toast.makeText(context,
                            "Transaction completed at: " +
                                    SOSRequestListAdapter.timestampConverter(timestamp), Toast.LENGTH_SHORT).show();
                    callback.run();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    //-------------------//------------------//-------------------//-------------------//-------------------
    // Booking MAINTENANCE service
    // Method for USER to create & push maintenance request to db
    public static void sendMaintenanceRequest (FirebaseDatabase rootNode,
                                               Context context,
                                               String vendorId,
                                               String userId,
                                               String requestId,
                                               long datetime,
                                               Runnable callback) {
        System.out.println(vendorId + " " + userId);

        // Get the root reference of chosen vendor
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        // Initialize sosRequest object
        MaintenanceRequest maintenanceRequest = new MaintenanceRequest(requestId, userId, vendorId, datetime);

        // Send request object to db
        vendorRef.child("maintenance").child("request").child(requestId).setValue(maintenanceRequest)
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "Request sent!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
        callback.run();
    }

    // Method for MECHANIC to respond to the maintenance request
    public static void respondMaintenanceRequest (FirebaseDatabase rootNode,
                                                  Context context,
                                                  String vendorId,
                                                  String requestId,
                                                  String respond,
                                                  String type) {
        // Get the root reference of chosen vendor
        DatabaseReference bookingRef = rootNode.getReference(vendorId).child("maintenance").child("request").child(requestId);

        // Respond to maintenance booking
        switch (type) {
            case "accepted":
                bookingRef.child("respond").setValue(respond);
                bookingRef.child("status").setValue("accepted")
                        .addOnCompleteListener(task -> Toast.makeText(context,
                                "MAINTENANCE BOOKING ACCEPTED!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "" +
                                e.getMessage(), Toast.LENGTH_SHORT).show());
                break;
            case "rejected":
                bookingRef.child("respond").setValue(respond);
                bookingRef.child("status").setValue("rejected")
                        .addOnCompleteListener(task -> Toast.makeText(context,
                                "MAINTENANCE BOOKING REJECTED!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "" +
                                e.getMessage(), Toast.LENGTH_SHORT).show());
                break;
        }
    }

    /** method for MECHANIC to GET all the pending maintenance request for him/herself
     * this is single GET method (no need to update on realtime)
      */
    public static void getAssignedMaintenanceRequest (FirebaseDatabase rootNode,
                                                      Context context,
                                                      String vendorId,
                                                      String userId,
                                                      ArrayList<EventTitle> resultContainer,
                                                      Runnable callback) {
        // Get the root reference of chosen vendor
        DatabaseReference maintenanceRef = rootNode.getReference(vendorId).child("maintenance").child("request");

        // Loop through and find the assigned request(s)
        maintenanceRef.get()
                .addOnSuccessListener(dataSnapshot -> {
                    resultContainer.clear();
                    ArrayList<EventTitle> tmp = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        MaintenanceRequest request = ds.getValue(MaintenanceRequest.class);

                        // If respond is mechanicId -> assigned one
                        if (Objects.requireNonNull(request).getRespond().equals(userId)) {
                            EventTitle et = new EventTitle(request.getDatetime(), "Maintenance", request.getStatus());
                            tmp.add(et);
                        }
                    }
                    // Sort collections by time created
                    Collections.sort(tmp, new EventTitleTimeStampComparator());

                    resultContainer.addAll(tmp);

                    // Run any callback (Eg. Update adapter)
                    callback.run();

                })
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
