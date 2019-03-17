package com.vatsal.android.digitaldetox.models;

public class CustomUsageEvents {
    public String packageName;
    public long timestamp;
    public String eventType;

    public CustomUsageEvents(String packageName, String eventType, long timestamp) {
        this.packageName = packageName;
        this.timestamp = timestamp;
        this.eventType = eventType;
    }
}
