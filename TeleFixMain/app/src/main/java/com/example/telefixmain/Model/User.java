package com.example.telefixmain.Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private boolean isVendor;
    private String vendorId;
    private ArrayList<String> registeredVehicles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isVendor() {
        return isVendor;
    }

    public void setVendor(boolean vendor) {
        isVendor = vendor;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public ArrayList<String> getRegisteredVehicles() {
        return registeredVehicles;
    }

    public void setRegisteredVehicles(ArrayList<String> registeredVehicles) {
        this.registeredVehicles = registeredVehicles;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", isVendor=" + isVendor +
                ", vendorId='" + vendorId + '\'' +
                '}';
    }
}
