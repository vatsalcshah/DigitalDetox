package com.vatsal.android.digitaldetox.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.vatsal.android.digitaldetox.R;
import com.vatsal.android.digitaldetox.database.DataBaseHelper;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This class manage information about the application with which the user interacts
 */
public class NotificationService extends Service {
    public DataBaseHelper db = null;
    public String currentApp = null;
    public Long currentUsage = 0L;
    public Long limit = 0L;

    /**
     * sets the database instance
     */
    public void setDb(){
        db = new DataBaseHelper(getBaseContext());
    }

    /**
     * get the time limit for the app tha is being used from the DataBase
     */
    public void setLimit(){
        limit = db.getLimit(getCurrentApp());
    }

    /**
     * returns the database instance
     * @return DataBaseHelper the smart phone's sql database
     */
    public DataBaseHelper getDb(){
        return db;
    }

    /**
     * returns the time limit the user set for the application
     * @return Long the time limit for the app
     */
    public Long getLimit(){
        return limit;
    }

    /**
     * returns the time usage for the app the user is interacting with
     * @return Long time usage for the app in minutes
     */
    public Long getCurrentUsage(){
        return currentUsage;
    }

    /**
     * returns the app that is being used
     * @return String the package name for the app
     */
    public String getCurrentApp(){
        return currentApp;
    }

    /**
     * sets the package name of the app that is being used
     */
    public void setCurrentApp(){
        //detects which application is being used
        UsageStatsManager usageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> sortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if ( !sortedMap.isEmpty()) {
                currentApp = sortedMap.get(sortedMap.lastKey()).getPackageName();
            }
        }
    }


    /**
     * builds and shows a notification
     * containing the app name and the time out of the time limit the user has set
     * @param currentUsage
     */
    public void buildNotification(Long currentUsage){
        //name of the application
        PackageManager packageManager= getApplicationContext().getPackageManager();
        String appName=null;
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(currentApp, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appName==null){appName=currentApp;}

        //notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.small_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.large_icon))
                        .setContentTitle("Warning")
                        .setContentText(appName+": "+currentUsage+" minutes out of "+getLimit())
                        .setDefaults(Notification.DEFAULT_SOUND);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    /**
     * sets the current usage for the app in foreground
     * @return Long current time usage for the app in minutes
     */
    public Long setCurrentUsage(){
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(System.currentTimeMillis());
        start.set(Calendar.DAY_OF_MONTH, -7);
        final UsageStatsManager statsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> stats = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start.getTimeInMillis(), end.getTimeInMillis());
        Collections.reverse(stats); //today first

        for(int i=0; i<stats.size(); ++i){
            if( String.valueOf(stats.get(i).getPackageName()).equals(currentApp)  ) {
                currentUsage = stats.get(i).getTotalTimeInForeground();
                currentUsage = currentUsage/60000;
                break;
            }
        }
        return currentUsage;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * sets the attributes of the class;
     * triggers a notification when a limit is reached
     * or the user keeps on using an app after the limit
     */
    @Override
    public void onCreate() {
        super.onCreate();
        setDb();
        setCurrentApp();
        setCurrentUsage();
        setLimit();

        //notify the user if the limit is reached, only if the limit exists
        if (getCurrentUsage() >= getLimit() && getLimit()!=-1 ){
            buildNotification(getCurrentUsage());
        }

    }
}