package com.example.telefixmain.Util;

import android.content.Context;
import android.widget.Toast;

import com.example.telefixmain.Model.User;
import com.example.telefixmain.Model.Vehicle;
import com.example.telefixmain.Model.Vendor;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class
DatabaseHandler {
    /**
     * Method to create new user on database
     */
    public static void createUser(FirebaseFirestore db,
                                  Context context,
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
    public static void getSingleUser(FirebaseFirestore db,
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
            }
        })
                .addOnFailureListener(e -> {
                    System.out.println(e.getMessage());
                    System.out.println("QUERY USER FAILED!");
                });
    }

    /**
     * Method to update user's info by email
     */
    public static void updateUser(FirebaseFirestore db, Context context,
                                  String id,
                                  String name,
                                  String phone,
                                  String email,
                                  boolean isMechanic,
                                  String vendorId,
                                  ArrayList<String> registeredVehicles,
                                  Runnable callback) {

        // Updated data container
        HashMap<String, Object> updatedData = new HashMap<>();

        // Inject updated data
        updatedData.put("id", id);
        updatedData.put("name", name);
        updatedData.put("phone", phone);
        updatedData.put("email", email);
        updatedData.put("isMechanic", isMechanic);
        updatedData.put("vendorId", vendorId);
        updatedData.put("registerVehicles", registeredVehicles);

        // search from database
        db.collection("users")
                .document(id)
                .set(updatedData)
                // run callback function if success
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.run();
                })
                .addOnFailureListener(e -> {
                    // this will be called when data updated unsuccessfully
                    System.out.println(e.getMessage());
                    Toast.makeText(context, "Updated user failed!", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Method to get vendors' info
     */
    public static void getAllVendors(FirebaseFirestore db,
                                     ArrayList<Vendor> resultContainer,
                                     Runnable callback) {
        // target "vendors" collection
        db.collection("vendors")
                .get() // Get data from Firestore
                .addOnCompleteListener(task -> {

                    // Loop through document and add into vendors container
                    for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        Vendor vendor = doc.toObject(Vendor.class);
                        resultContainer.add(vendor);
                    }
                    // run call back function
                    callback.run();

                    // success msg
                    System.out.println("FETCH VENDORS SUCCESSFULLY!");
                })
                .addOnFailureListener(e -> {
                    // fail msg
                    System.out.println("FETCH VENDORS FAILED!");
                    System.out.println("FETCH VENDORS ERROR: " + e.getMessage());
                });
    }

    /**
     * Method to get vendors' price list
     */
    @SuppressWarnings("unchecked")
    public static void getVendorPriceListById(FirebaseFirestore db,
                                              String vendorId,
                                              HashMap<String, String> inspectionPriceContainer,
                                              HashMap<String, String> repairPriceContainer,
                                              Runnable callback) {
        // target "vendors" collection
        db.collection("vendors")
                .document(vendorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // result document
                        DocumentSnapshot document = task.getResult();

                        // map of inspection prices
                        HashMap<String, Object> inspectionPriceMap = (HashMap<String, Object>)
                                Objects.requireNonNull(document.getData()).get("inspectionPrice");

                        // map of repair prices
                        HashMap<String, Object> repairPriceMap = (HashMap<String, Object>)
                                Objects.requireNonNull(document.getData()).get("repairPrice");

                        // inject data into containers
                        for (String ip : Objects.requireNonNull(inspectionPriceMap).keySet())
                            inspectionPriceContainer.put(ip, (String) inspectionPriceMap.get(ip));

                        for (String rp : Objects.requireNonNull(repairPriceMap).keySet())
                            repairPriceContainer.put(rp, (String) repairPriceMap.get(rp));

                        // run callback function
                        callback.run();

                        // success msg
                        System.out.println("FETCH VENDOR'S PRICE LIST SUCCESSFULLY!");
                    }
                })
                .addOnFailureListener(e -> {
                    // fail msg
                    System.out.println("FETCH VENDOR'S PRICE LIST FAILED!");
                    System.out.println("FETCH VENDOR'S PRICE LIST ERROR: " + e.getMessage());
                });
    }

    /**
     * Method to register a new vehicle
     */
    public static void createVehicle(FirebaseFirestore db, Context context,
                                     String userId,
                                     String vehicleBrand,
                                     String vehicleModel,
                                     String vehicleYear,
                                     String vehicleColor,
                                     String vehicleNumberPlate,
                                     Runnable callback) {
        // Create a new user
        HashMap<String, Object> data = new HashMap<>();

        // inject new vehicle data
        String vehicleId = UUID.randomUUID().toString();
        data.put("userId", userId);
        data.put("vehicleId", vehicleId);
        data.put("vehicleBrand", vehicleBrand);
        data.put("vehicleModel", vehicleModel);
        data.put("vehicleYear", vehicleYear);
        data.put("vehicleColor", vehicleColor);
        data.put("vehicleNumberPlate", vehicleNumberPlate);

        // add new vehicle data to database
        db.collection("vehicles")
                .document(vehicleId)
                .set(data)
                // run callback function if success
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateUserVehicleList(db, context, userId, vehicleId, callback);
                    }
                })
                .addOnFailureListener(e -> {
                    // this will be called when data updated unsuccessfully
                    System.out.println(e.getMessage());
                    Toast.makeText(context, "Register vehicle failed!", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Method to update register vehicle array of user based on user's id
     */
    public static void updateUserVehicleList(FirebaseFirestore db, Context context,
                                             String userId, String vehicleId,
                                             Runnable callback) {
        // find user and update vehicle list
        db.collection("users")
                .document(userId)
                .update("registerVehicles", FieldValue.arrayUnion(vehicleId))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Register vehicle successfully!", Toast.LENGTH_SHORT).show();

                        // run callback
                        callback.run();
                    }
                })
                .addOnFailureListener(e -> {
                    // this will be called when data updated unsuccessfully
                    System.out.println(e.getMessage());
                    Toast.makeText(context, "Register vehicle failed!", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Method to get user's registered vehicles list
     */
    @SuppressWarnings("unchecked")
    public static void getUserVehicleList(FirebaseFirestore db,
                                          Context context,
                                          String userId,
                                          ArrayList<String> vehiclesIdResult,
                                          ArrayList<Vehicle> vehiclesResult,
                                          Runnable callback) {
        // find user and get vehicle list
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // result document
                        DocumentSnapshot documentSnapshot = task.getResult();

                        // casting vehicle to array list
                        ArrayList<String> vehicleTempList =
                                (ArrayList<String>) documentSnapshot.get("registerVehicles");

                        // populate vehicle id array list
                        vehiclesIdResult.addAll(Objects.requireNonNull(vehicleTempList));

                        // do only if there is any vehicle id, otherwise cut short the process
                        if (vehiclesIdResult.size() > 0) {
                            // populate vehicles object list
                            for (String vId : vehiclesIdResult) {
                                if (vehiclesIdResult.indexOf(vId) == vehiclesIdResult.size() - 1) {
                                    // run callback if it is the last id
                                    getSingleVehicle(db, vId, vehiclesResult, callback);
                                } else {
                                    getSingleVehicle(db, vId, vehiclesResult, () -> {
                                    });
                                }
                            }
                        } else {
                            callback.run();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println(e.getMessage());
                    Toast.makeText(context, "Get vehicle list failed!", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Method to get single vehicle from database based on id
     */
    public static void getSingleVehicle(FirebaseFirestore db,
                                        String vehicleId,
                                        ArrayList<Vehicle> resultContainer,
                                        Runnable callback) {
        // document reference instance
        DocumentReference docRef = db.collection("vehicles").document(vehicleId);

        // start querying by id
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Document found in the offline cache
                DocumentSnapshot document = task.getResult();
                Vehicle vehicle = document.toObject(Vehicle.class);

                // add found user object to container
                resultContainer.add(vehicle);

                // log
                callback.run();

                // success msg
                System.out.println("QUERY VEHICLE SUCCESSFULLY!");
            }
        })
                .addOnFailureListener(e -> {
                    System.out.println(e.getMessage());
                    System.out.println("QUERY VEHICLE FAILED!");
                });
    }
}
