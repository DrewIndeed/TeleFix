package com.example.telefixmain.Model.Booking;

public class SOSProgress {
    private long startMovingTimestamp;
    private long startFixingTimestamp;
    private long startBillingTimestamp;
    private long abortTime;
    private long confirmBillingTime;

    public SOSProgress() {}

    public SOSProgress(long startMovingTimestamp) {
        this.startMovingTimestamp = startMovingTimestamp;
        this.abortTime = 0;
        this.startBillingTimestamp = 0;
        this.startFixingTimestamp = 0;
        this.confirmBillingTime = 0;
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
        return abortTime;
    }

    public void setAbortedTime(long abortedTime) {
        this.abortTime = abortedTime;
    }

    public long getConfirmBillingTime() {
        return confirmBillingTime;
    }

    public void setConfirmBillingTime(long confirmBillingTime) {
        this.confirmBillingTime = confirmBillingTime;
    }
}
