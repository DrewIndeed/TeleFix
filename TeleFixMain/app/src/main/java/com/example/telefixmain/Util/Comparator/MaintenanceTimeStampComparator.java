package com.example.telefixmain.Util.Comparator;

import com.example.telefixmain.Model.Booking.MaintenanceRequest;

import java.util.Comparator;

public class MaintenanceTimeStampComparator implements Comparator<MaintenanceRequest> {
    @Override
    public int compare(MaintenanceRequest rq1, MaintenanceRequest rq2) {
         return Long.compare(rq1.getDatetime(), rq2.getDatetime());
    }
}
