package com.example.telefixmain.Util;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.telefixmain.Model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler {
    /**
     * Method to create new user on database
     */
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

    /**
     * Method to get single user from database based on id
     */
    public static void getSingleUser(FirebaseFirestore db, Context context,
                                     String id, ArrayList<User> resultContainer,
                                     Runnable callback) {
        // document reference instance
        DocumentReference docRef = db.collection("users").document(id);

        // start querying by id
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Document found in the offline cache
                DocumentSnapshot document = task.getResult();
                User user = document.toObject(User.class);

                // add found user object to container
                resultContainer.add(user);

                // run call back function
                callback.run();

                // success msg
                System.out.println("QUERY USER SUCCESSFULLY!");
            } else {
                Log.d(TAG, "Cached get failed: ", task.getException());
            }
        });
    }
}
