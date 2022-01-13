package com.example.telefixmain.Model.Booking;

public class MaintenanceRequest {
    private String requestId;
    private String userId;
    private String vendorId;
    private String mechanicId;
    private long date;
    private long time;
    String status;

    public MaintenanceRequest(String requestId, String userId, String vendorId, long date, long time) {
        this.requestId = requestId;
        this.userId = userId;
        this.vendorId = vendorId;
        this.mechanicId = "";
        this.date = date;
        this.time = time;
        this.status = "on-going";
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
