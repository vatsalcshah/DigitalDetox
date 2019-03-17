package com.vatsal.android.digitaldetox.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vatsal.android.digitaldetox.services.Monitor;

/**
 * This class detects the device booting
 * and starts a service for monitoring real time apps usage
 */
public class MonitorBootReceiver extends BroadcastReceiver {

    /**
     * starts the monitor service to monitor real time apps usage
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            //starts a service for monitoring real time app usage
            Intent monitorInten = new Intent(context, Monitor.class);
            context.startService(monitorInten);
        }
    }

}