package com.vatsal.android.digitaldetox.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.vatsal.android.digitaldetox.R;
import com.vatsal.android.digitaldetox.database.Database;
import com.vatsal.android.digitaldetox.models.CustomUsageEvents;
import com.vatsal.android.digitaldetox.models.DisplayEventEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class FormatEventsViewModel extends AndroidViewModel {

    private static HashMap<String, Pair<Drawable, String>> iconMap = new HashMap<>(150);
    public int mDateOffset = 0;
    public boolean isJustNoOffset = false;
    public boolean isDateLayoutVisible = true;
    public String formattedDate;
    private MutableLiveData<List<DisplayEventEntity>> displayLiveData = new MutableLiveData<>();
    private MutableLiveData<List<DisplayEventEntity>> appCachedLiveData = new MutableLiveData<>();
    private Database db;
    private PackageManager pm;

    public FormatEventsViewModel(Application application) {
        super(application);
    }


    private List<CustomUsageEvents> getUsageEvents(UsageStatsManager mUsageStatsManager,
                                                   String[] excludePackages, long startTime, long endTime) {
        List<CustomUsageEvents> copy = new ArrayList<>();
        UsageEvents.Event event = new UsageEvents.Event();
        String eventType;
        UsageEvents queryUsageEvents = mUsageStatsManager.queryEvents(startTime, endTime);

        if (!queryUsageEvents.hasNextEvent())
            return null;

        outer:
        while (queryUsageEvents.getNextEvent(event)) {
            for (String excludePackage : excludePackages)
                if (excludePackage.equals(event.getPackageName()))
                    continue outer;
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                eventType = Constants.FG;
                copy.add(new CustomUsageEvents(event.getPackageName(),
                        eventType, event.getTimeStamp()));
            } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                eventType = Constants.BG;
                copy.add(new CustomUsageEvents(event.getPackageName(),
                        eventType, event.getTimeStamp()));
            }

            event = new UsageEvents.Event();
        }

        Log.i("GAAH", "getUsageEvents: size " + copy.size());
        return copy;
    }

    /*
    * Merges a background event and a foreground event of the same package to a CustomUsageEvents
    * Merging only happens if a FG immediately follows a BG
    */
    private List<DisplayEventEntity> mergeBgFg(List<CustomUsageEvents> events) {
        List<DisplayEventEntity> copy = new ArrayList<>();
        boolean skip = false;

        for (int i = 0; i < events.size() - 1; i++) {
            if (skip) {
                skip = false;
                continue;
            }

            CustomUsageEvents thisEvent = events.get(i);
            CustomUsageEvents nextEvent = events.get(i + 1);

            if (thisEvent.packageName.equals(nextEvent.packageName)) {
                if (Constants.BG.equals(thisEvent.eventType)
                        && Constants.FG.equals(nextEvent.eventType)) {

                    DisplayEventEntity event = new DisplayEventEntity(thisEvent.packageName,
                            nextEvent.timestamp, thisEvent.timestamp);
                    insertIconName(event, true);
                    copy.add(event);
                    skip = true;
                } else if (i == 0) {
                    // Making sure that Ongoing events in the middle of the list get dropped
                    DisplayEventEntity event = new DisplayEventEntity(thisEvent.packageName,
                            nextEvent.timestamp, 1);
                    insertIconName(event, true);
                    copy.add(event);
                }
            } else if (i == 0) {
                // Making sure that Ongoing events in the middle of the list get dropped
                DisplayEventEntity event = new DisplayEventEntity(thisEvent.packageName,
                        nextEvent.timestamp, 1);
                insertIconName(event, true);
                copy.add(event);
            }
        }
        //Log.i("GAAH2", "mergeBgFg: Size " + copy.size());
        return copy;
    }


    /*
    * Merges items from the same package name together if the events are less than MIN_TIME_DIFFERENCE ms apart
    * Also drops items with a duration less than MIN_TIME_TOLERANCE ms
    */
    private List<DisplayEventEntity> mergeSame(List<DisplayEventEntity> events) {
        final long MIN_TIME_DIFFERENCE = 1000 * 5;
        final int MIN_TIME_TOLERANCE = 600;

        DisplayEventEntity previous = null;

        if (events == null)
            return null;

        Iterator<DisplayEventEntity> iterator = events.iterator();
        while (iterator.hasNext()) {
            if (previous == null) {
                previous = iterator.next();
                continue;
            }

            DisplayEventEntity thisEvent = iterator.next();

            // THIS WILL MISS THE LAST EVENT
            if (thisEvent.endTime > 0 && (thisEvent.endTime - thisEvent.startTime) < MIN_TIME_TOLERANCE) {
                iterator.remove();
            } else {
                if (previous.packageName.equals(thisEvent.packageName)) {
                    if (previous.startTime - thisEvent.endTime > MIN_TIME_DIFFERENCE) {
                        previous = thisEvent;
                    } else {
                        previous.startTime = thisEvent.startTime;
                        iterator.remove();
                    }
                } else
                    previous = thisEvent;
            }
        }
        Log.i("GAAH2", "mergeSame: new Size " + events.size());
        return events;
    }

    private void insertIconName(DisplayEventEntity event, boolean needsAppName) {
        Pair<Drawable, String> pair = iconMap.get(event.packageName);
        if (pair != null) {
            event.appIcon = pair.first;
            if (needsAppName) {
                if (pair.second != null)
                    event.appName = pair.second;
                else {
                    event.appName = getAppName(event.packageName);
                    iconMap.remove(event.packageName);
                    iconMap.put(event.packageName, new Pair<>(event.appIcon, event.appName));
                }
            }
        } else {
            try {
                event.appIcon = pm.
                        getApplicationIcon(event.packageName);
            }
            catch (PackageManager.NameNotFoundException e) {
                Drawable drawable = getApplication()
                        .getDrawable(R.drawable.ic_launcher);
                if (drawable != null)
                    event.appIcon = drawable;
            }
            if (needsAppName)
                event.appName = getAppName(event.packageName);
            iconMap.put(event.packageName, new Pair<>(event.appIcon, event.appName));
        }

    }

    private String getAppName(String packageName) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = pm
                    .getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
        if (applicationInfo != null) {
            String name = pm.getApplicationLabel(applicationInfo).toString();
            return "".equals(name) ? packageName : name;
        } else
            return packageName;
    }

    public LiveData<List<DisplayEventEntity>> getDisplayUsageEventsList() {
        return displayLiveData;
    }

    public LiveData<List<DisplayEventEntity>> getAppDetailEventsList() {
        return appCachedLiveData;
    }

    private void insertInDb(List<DisplayEventEntity> events) {
        if (db == null)
            return;
        new Thread(() -> db.dao().insertEvent(events)).start();
    }

    private void findEvents(UsageStatsManager usageStatsManager,
                            String[] excludePackages, long startTime, long endTime,
                            List<DisplayEventEntity> oldEntities, final int NUMBER_TO_REMOVE, boolean isIconNeeded) {

        List<CustomUsageEvents> usageEvents = getUsageEvents(usageStatsManager, excludePackages, startTime, endTime);
        if (usageEvents == null) {
            displayLiveData.setValue(null);
            return;
        }

        Collections.sort(usageEvents, (left, right) ->
                Long.compare(right.timestamp, left.timestamp));

        List<DisplayEventEntity> merged = mergeBgFg(usageEvents);
        merged = mergeSame(merged);

        if (oldEntities != null) {
            removeUnstables(oldEntities, NUMBER_TO_REMOVE, merged, isIconNeeded);
        } else {
            displayLiveData.setValue(merged);
            insertInDb(merged);
        }
    }

    private void removeUnstables(List<DisplayEventEntity> eventsInDb, final int NUMBER_TO_REMOVE,
                                 List<DisplayEventEntity> merged, boolean isIconNeeded) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> remove = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Iterator<DisplayEventEntity> iterator = eventsInDb.iterator();
                for (int i = 0; i < NUMBER_TO_REMOVE && iterator.hasNext(); i++) {
                    DisplayEventEntity current = iterator.next();
                    db.dao().deleteEvent(current);
                    Log.i("removed", "removeUnstables: deleted " + current.appName + current.startTime);
                    iterator.remove();
                }
                if (isIconNeeded)
                    for (DisplayEventEntity displayEventEntity : eventsInDb) {
                        insertIconName(displayEventEntity, false);
                    }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                for (int i = merged.size() - 1; i >= 0; i--) {
                    eventsInDb.add(0, merged.get(i));
                    //Log.d("merge", "onPostExecute: number: " + i + " merged: " + merged.get(i).appName);
                }
                displayLiveData.setValue(eventsInDb);
                insertInDb(merged);
                super.onPostExecute(aVoid);
            }
        };

        remove.execute();
    }

    public void setDisplayUsageEventsList(UsageStatsManager usageStatsManager,
                                          String[] excludePackages, long startTime, long endTime,
                                          boolean isIconNeeded, boolean isCachedOnly) {

        if (startTime >= endTime) {
            displayLiveData.setValue(null);
            return;
        }
        if (db == null)
            db = Room.databaseBuilder(getApplication(), Database.class, "eventsDb").build();
        if (pm == null)
            pm = getApplication().getPackageManager();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, List<DisplayEventEntity>> populateList = new AsyncTask<Void, Void, List<DisplayEventEntity>>() {
            private int numberToRemove = 3;

            @Override
            protected List<DisplayEventEntity> doInBackground(Void... voids) {
                return db.dao().getEvents(startTime, endTime);
            }

            @Override
            protected void onPostExecute(List<DisplayEventEntity> eventsInDb) {
                if (eventsInDb == null || eventsInDb.size() == 0) {
                    Log.d("GAAH", "setDisplayUsageEventsList: null " + startTime + " " + endTime);
                    findEvents(usageStatsManager, excludePackages, startTime, endTime,
                            null, -1, isIconNeeded);
                } else {
                    for (int i = 0; i < 2 && i < eventsInDb.size(); i++) {
                        Log.i("database", "onPostExecute: " + eventsInDb.get(i).appName + " " + eventsInDb.get(i).startTime);
                    }

                    if (numberToRemove >= eventsInDb.size())
                        numberToRemove = eventsInDb.size() - 1;
                    long newStartTime = eventsInDb.get(numberToRemove).endTime + 20;

                    if (isCachedOnly) {
                        for (DisplayEventEntity displayEventEntity : eventsInDb) {
                            insertIconName(displayEventEntity, false);
                        }
                        displayLiveData.setValue(eventsInDb);
                        return;
                    }

                    if (newStartTime >= endTime) {
                        displayLiveData.setValue(eventsInDb);
                        return;
                    }
                    findEvents(usageStatsManager, excludePackages, newStartTime, endTime,
                            eventsInDb, numberToRemove, isIconNeeded);
                }
            }
        };

        populateList.execute();
    }


    /*
     * Return cached usages from the db.
     */
    public void setCachedEventsList(long startTime, long endTime) {

        if (startTime >= endTime) {
            appCachedLiveData.setValue(null);
            return;
        }
        if (db == null)
            db = Room.databaseBuilder(getApplication(), Database.class, "eventsDb").build();
        if (pm == null)
            pm = getApplication().getPackageManager();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, List<DisplayEventEntity>> populateList = new AsyncTask<Void, Void, List<DisplayEventEntity>>() {

            @Override
            protected List<DisplayEventEntity> doInBackground(Void... voids) {
                return db.dao().getEvents(startTime, endTime);
            }

            @Override
            protected void onPostExecute(List<DisplayEventEntity> eventsInDb) {
                // for (int i = 0; i < 5 && i < eventsInDb.size(); i++) {
                //     Log.i("database", "onPostExecute: " + eventsInDb.get(i).appName + " " + eventsInDb.get(i).startTime);
                // }

                for (DisplayEventEntity displayEventEntity : eventsInDb) {
                    insertIconName(displayEventEntity, false);
                }
                appCachedLiveData.setValue(eventsInDb);

                super.onPostExecute(eventsInDb);
            }
        };

        populateList.execute();
    }
}
