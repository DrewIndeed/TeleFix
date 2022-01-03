package com.example.telefixmain.Util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.telefixmain.Model.Booking.SOSBilling;
import com.example.telefixmain.Model.Booking.SOSMetadata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;

import java.util.UUID;

public class BookingHandler {
    public static void sendSOSRequest (FirebaseDatabase rootNode,
                                       Context context,
                                       String vendorId,
                                       String userId) {

        System.out.println(vendorId + " " + userId);
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        SOSMetadata sosRequest = new SOSMetadata(userId);
        String requestId = UUID.randomUUID().toString();

        vendorRef.child("sos").child("metadata").child(requestId).setValue(sosRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context,"Request has been sent successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
