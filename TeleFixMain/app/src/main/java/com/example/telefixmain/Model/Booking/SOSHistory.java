package com.example.telefixmain.Model.Booking;

import java.util.ArrayList;

public class SOSHistory {
    private String requestId;
    private String userId;
    private String vendorId;
    private String mechanicId;
    private long startTime;
    private long endTime;
    private ArrayList<SOSBilling> data;
    private int total;
    private String type;  // sos or maintenance
    private String status; // successful or aborted

}
