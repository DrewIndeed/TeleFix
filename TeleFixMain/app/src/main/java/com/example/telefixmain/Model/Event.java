package com.example.telefixmain.Model;

import com.example.telefixmain.Model.Booking.Billing;

import java.util.ArrayList;

public class Event {
    private String requestId;
    private String userId;
    private String vendorId;
    private String mechanicId;
    private String type;
    private String status;
    private long startTime;
    private long endTime;
    private ArrayList<Billing> billingData;
    private int total;

    public Event () {}
    
    public Event(String requestId, String userId, String vendorId, String mechanicId, String type, String status, long startTime, long endTime, ArrayList<Billing> billingData, int total) {
        this.requestId = requestId;
        this.userId = userId;
        this.vendorId = vendorId;
        this.mechanicId = mechanicId;
        this.type = type;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.billingData = billingData;
        this.total = total;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Billing> getBillingData() {
        return billingData;
    }

    public void setBillingData(ArrayList<Billing> billingData) {
        this.billingData = billingData;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
