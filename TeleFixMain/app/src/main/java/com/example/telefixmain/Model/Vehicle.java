package com.example.telefixmain.Model;

import androidx.annotation.NonNull;

public class Vehicle {
    private String vehicleBrand, vehicleModel, vehicleYear, vehicleColor;
    private String vehicleNumberPlate, vehicleId;

    public Vehicle(String vehicleBrand, String vehicleModel, String vehicleYear,
                   String vehicleColor, String vehicleNumberPlate, String vehicleId) {
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
        this.vehicleYear = vehicleYear;
        this.vehicleColor = vehicleColor;
        this.vehicleNumberPlate = vehicleNumberPlate;
        this.vehicleId = vehicleId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(String vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getVehicleNumberPlate() {
        return vehicleNumberPlate;
    }

    public void setVehicleNumberPlate(String vehicleNumberPlate) {
        this.vehicleNumberPlate = vehicleNumberPlate;
    }

    @NonNull
    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleBrand='" + vehicleBrand + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", vehicleYear='" + vehicleYear + '\'' +
                ", vehicleColor='" + vehicleColor + '\'' +
                ", vehicleNumberPlate='" + vehicleNumberPlate + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                '}';
    }
}
