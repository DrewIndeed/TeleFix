package com.example.telefixmain.Model.Booking;

public class MaintenanceRequest {
    private String requestId;
    private String userId;
    private String vendorId;
    private String respond;
    private long datetime;
    private String status;

    public MaintenanceRequest() {}

    public MaintenanceRequest(String requestId, String userId, String vendorId, long datetime) {
        this.requestId = requestId;
        this.userId = userId;
        this.vendorId = vendorId;
        this.respond = "";
        this.datetime = datetime;
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

    public String getRespond() {
        return respond;
    }

    public void setRespond(String respond) {
        this.respond = respond;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
