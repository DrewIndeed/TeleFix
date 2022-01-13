package com.example.telefixmain.Util.Comparator;

import com.example.telefixmain.Model.Booking.SOSRequest;

import java.util.Comparator;

public class RequestTimeStampComparator implements Comparator<SOSRequest> {
    @Override
    public int compare(SOSRequest request1, SOSRequest request2) {
        return Integer.parseInt(
                Boolean.toString(request1.getTimestampCreated() >= request2.getTimestampCreated())
        );
    }
}
