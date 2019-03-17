package com.vatsal.android.digitaldetox.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vatsal.android.digitaldetox.R;
import com.vatsal.android.digitaldetox.utils.FormatEventsViewModel;
import com.vatsal.android.digitaldetox.utils.Tools;

import java.util.Calendar;



public class PopulateDatabaseService extends Service {

    protected String[] excludePackages;
    FormatEventsViewModel mViewModel;
    private UsageStatsManager mUsageStatsManager;
    private Calendar cal = Calendar.getInstance();
    private Handler mDatabaseHandler;
    private Runnable mDatabaseRunnable;

    private BroadcastReceiver mUserPresentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("GAAH", "onReceive: UserPresent");
            populateDatabase();
        }
    };

    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("GAAH", "onReceive: OFF");
            mDatabaseHandler.removeCallbacks(mDatabaseRunnable);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT > 21)
            mUsageStatsManager = (UsageStatsManager) getApplicationContext()
                    .getSystemService(Context.USAGE_STATS_SERVICE);
        else
            mUsageStatsManager = (UsageStatsManager) getApplicationContext()
                    .getSystemService("usagestats");

        mDatabaseHandler = new Handler();
        mViewModel = new FormatEventsViewModel(getApplication());

        excludePackages = getApplicationContext().getResources().getStringArray(R.array.exclude_packages);
        excludePackages[excludePackages.length - 1] = Tools.findLauncher(getApplicationContext());

        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);

        IntentFilter userPresentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        getApplicationContext().registerReceiver(mUserPresentReceiver, userPresentFilter);
        IntentFilter screenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        getApplicationContext().registerReceiver(mScreenOffReceiver, screenOffFilter);

        populateDatabase();
    }

    private void populateDatabase() {
        final long DELAY = 10 * 60 * 1000;
        final long FIRST_START_DELAY = 10 * 1000;

        mDatabaseRunnable = new Runnable() {
            @Override
            public void run() {
                if (mViewModel != null) {
                    mViewModel.setDisplayUsageEventsList(mUsageStatsManager, excludePackages,
                            cal.getTimeInMillis(), System.currentTimeMillis(), false, false);
                    mDatabaseHandler.postDelayed(this, DELAY);
                }
            }
        };
        mDatabaseHandler.postDelayed(mDatabaseRunnable, FIRST_START_DELAY);
    }

    @Override
    public void onDestroy() {
        getApplicationContext().unregisterReceiver(mUserPresentReceiver);
        getApplicationContext().unregisterReceiver(mScreenOffReceiver);
        super.onDestroy();
    }
}
