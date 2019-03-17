package com.vatsal.android.digitaldetox.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

@Entity(tableName = "events", primaryKeys = {"startTime", "appName"})
public class DisplayEventEntity {

    @NonNull
    public String appName;
    public long startTime;
    public long endTime;
    public int ongoing;
    public String packageName;

    @Ignore
    public Drawable appIcon;

    public DisplayEventEntity() {

    }

    @Ignore
    public DisplayEventEntity(String packageName, long startTime, long endTime) {
        this.packageName = packageName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Ignore
    public DisplayEventEntity(String packageName, long startTime, int ongoing) {
        this.packageName = packageName;
        this.startTime = startTime;
        this.ongoing = ongoing;
    }
}
