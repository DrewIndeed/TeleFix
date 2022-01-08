package com.example.telefixmain.Model.Booking;

public class SOSRequest {
    // Public attribute to allow Firebase Realtime Database push to / retrieve from DocumentReference
    private String userId;
    private String mechanicId;
    private long timestampCreated;
    private double lat;
    private double lng;

    public SOSRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(SOSMetadata.class)
    }

    public SOSRequest(String userId, long timestampCreated, double lat, double lng) {
        this.mechanicId = "";
        this.userId = userId;
        this.timestampCreated = timestampCreated;
        this.lat = lat;
        this.lng = lng;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    public long getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(long timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
