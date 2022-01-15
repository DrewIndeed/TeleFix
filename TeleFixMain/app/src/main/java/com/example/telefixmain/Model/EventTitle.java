package com.example.telefixmain.Model;

public class EventTitle {
    private long timestamp;
    private String type;
    private String status;

    public EventTitle () {}

    public EventTitle(long timestamp, String type, String status) {
        this.timestamp = timestamp;
        this.type = type;
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
