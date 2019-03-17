package com.vatsal.android.digitaldetox.models;

import java.util.ArrayList;
import java.util.List;


public class AppFilteredEvents {
    public List<DisplayEventEntity> appEvents = new ArrayList<>();
    public List<DisplayEventEntity> otherEvents = new ArrayList<>();
    public long startTime;
    public long endTime;
}
