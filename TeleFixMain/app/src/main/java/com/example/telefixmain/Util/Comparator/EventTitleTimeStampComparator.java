package com.example.telefixmain.Util.Comparator;

import com.example.telefixmain.Model.Booking.MaintenanceRequest;
import com.example.telefixmain.Model.EventTitle;

import java.util.Comparator;

public class EventTitleTimeStampComparator implements Comparator<EventTitle> {
    @Override
    public int compare(EventTitle rq1, EventTitle rq2) {
        return Long.compare(rq1.getTimestamp(), rq2.getTimestamp());
    }
}
