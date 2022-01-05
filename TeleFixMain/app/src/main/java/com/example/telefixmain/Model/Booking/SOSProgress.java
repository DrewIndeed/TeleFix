package com.example.telefixmain.Model.Booking;

public class SOSProgress {
    private long startMovingTimestamp;
    private long startFixingTimestamp;
    private long startBillingTimestamp;
    private boolean isAborted;

    public SOSProgress() {}

    public SOSProgress(long startMovingTimestamp) {
        this.startMovingTimestamp = startMovingTimestamp;
        this.isAborted = false;
    }

    public long getStartMovingTimestamp() {
        return startMovingTimestamp;
    }

    public void setStartMovingTimestamp(long startMovingTimestamp) {
        this.startMovingTimestamp = startMovingTimestamp;
    }

    public long getStartFixingTimestamp() {
        return startFixingTimestamp;
    }

    public void setStartFixingTimestamp(long startFixingTimestamp) {
        this.startFixingTimestamp = startFixingTimestamp;
    }

    public long getStartBillingTimestamp() {
        return startBillingTimestamp;
    }

    public void setStartBillingTimestamp(long startBillingTimestamp) {
        this.startBillingTimestamp = startBillingTimestamp;
    }

    public boolean isAborted() {
        return isAborted;
    }

    public void setAborted(boolean aborted) {
        isAborted = aborted;
    }
}
