package com.example.telefixmain.Util;

import android.content.Context;
import android.widget.Toast;

import com.example.telefixmain.Model.Booking.SOSMetadata;
import com.example.telefixmain.Model.Booking.SOSProgress;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookingHandler {
    public static void sendSOSRequest(FirebaseDatabase rootNode,
                                      Context context,
                                      String vendorId,
                                      String userId,
                                      String requestId,
                                      long timeCreated,
                                      Runnable callback) {

        System.out.println(vendorId + " " + userId);
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        SOSMetadata sosRequest = new SOSMetadata(userId, timeCreated);

        vendorRef.child("sos").child("metadata").child(requestId).setValue(sosRequest)
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "Request has been sent successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
        callback.run();
    }

    public static void createProgressTracking (FirebaseDatabase rootNode,
                                               Context context,
                                               String vendorId,
                                               String requestId,
                                               long timeAccepted) {
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        SOSProgress sosProgress = new SOSProgress(timeAccepted);

        vendorRef.child("sos").child("metadata").child(requestId).setValue(sosProgress)
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "Progress tracking has been initialized successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static void removeSOSRequest(FirebaseDatabase rootNode,
                                        Context context,
                                        String vendorId,
                                        String requestId) {

        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        vendorRef.child("sos").child("metadata").child(requestId).removeValue()
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "Request has been cleared successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
