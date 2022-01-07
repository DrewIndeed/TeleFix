package com.example.telefixmain.Model.Booking;

public class SOSProgress {
    private long startMovingTimestamp;
    private long startFixingTimestamp;
    private long startBillingTimestamp;
    private long abortedTime;

    public SOSProgress() {}

    public SOSProgress(long startMovingTimestamp) {
        this.startMovingTimestamp = startMovingTimestamp;
        this.abortedTime = 0;
        this.startBillingTimestamp = 0;
        this.startFixingTimestamp = 0;
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

    public long getAbortedTime() {
        return abortedTime;
    }

    public void setAbortedTime(long abortedTime) {
        this.abortedTime = abortedTime;
    }
}
