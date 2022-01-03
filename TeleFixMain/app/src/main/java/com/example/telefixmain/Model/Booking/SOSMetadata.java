package com.example.telefixmain.Model.Booking;

public class SOSMetadata {
    // Public attribute to allow Firebase Realtime Database push to / retrieve from DocumentReference
    public String userId;
    public String mechanicId;
    public long timestampCreated;

    public SOSMetadata() {
        // Default constructor required for calls to DataSnapshot.getValue(SOSMetadata.class)
    }

    public SOSMetadata(String userId, long timestampCreated) {
        this.mechanicId = "";
        this.userId = userId;
        this.timestampCreated = timestampCreated;
    }
}
