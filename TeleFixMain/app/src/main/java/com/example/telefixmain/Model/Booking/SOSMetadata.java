package com.example.telefixmain.Model.Booking;

public class SOSMetadata {
    // Public attribute to allow Firebase Realtime Database push to / retrieve from DocumentReference
    private String userId;
    private String mechanicId;
    private long timestampCreated;

    public SOSMetadata() {
        // Default constructor required for calls to DataSnapshot.getValue(SOSMetadata.class)
    }

    public SOSMetadata(String userId, long timestampCreated) {
        this.mechanicId = "";
        this.userId = userId;
        this.timestampCreated = timestampCreated;
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
}
