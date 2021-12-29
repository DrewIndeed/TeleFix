package com.example.telefixmain.Util;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.telefixmain.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler {
    public static void createUserOnDatabase(FirebaseFirestore db, Context context,
                                     String id,
                                     String name,
                                     String phone,
                                     String email,
                                     boolean isMechanic,
                                     String vendorId) {

        // Create a new user
        HashMap<String, Object> data = new HashMap<>();

        // put zone data into temp data HashMap
        data.put("id", id);
        data.put("name", name);
        data.put("phone", phone);
        data.put("email", email);
        data.put("isMechanic", isMechanic);
        data.put("vendorId", vendorId);
        data.put("registerVehicles", new ArrayList<String>());

        // Add a new document with a generated ID
        db.collection("users").document(id).set(data)
                .addOnSuccessListener(documentReference -> {
                    // this will be called when data added successfully
                    Toast.makeText(context, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // this will be called when there is an error while adding
                    Toast.makeText(context, "Signed up failed!", Toast.LENGTH_SHORT).show();
                });
    }

    public static void getSingleUser(FirebaseFirestore db, Context context, String id, ArrayList<User> resultContainer) {
        DocumentReference docRef = db.collection("users").document(id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    User user = document.toObject(User.class);
                    resultContainer.add(user);
                    Toast.makeText(context,"Query user successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });
    }
}
