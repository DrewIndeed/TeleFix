package com.example.telefixmain.Model;

import androidx.annotation.NonNull;

public class Vehicle {
    private String vehicleBrand, vehicleModel, vehicleYear, vehicleColor;
    private String vehicleNumberPlate;

    public Vehicle(String brand, String model, String year, String color, String vehicleNumberPlate) {
        this.vehicleBrand = brand;
        this.vehicleModel = model;
        this.vehicleYear = year;
        this.vehicleColor = color;
        this.vehicleNumberPlate = vehicleNumberPlate;
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
                '}';
    }
}
