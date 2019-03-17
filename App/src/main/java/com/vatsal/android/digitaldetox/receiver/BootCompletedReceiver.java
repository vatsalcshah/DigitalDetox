package com.vatsal.android.digitaldetox.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vatsal.android.digitaldetox.services.PopulateDatabaseService;

/**
 * Created by j on 13/7/17.
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent launchIntent = new Intent(context, PopulateDatabaseService.class);
        context.stopService(launchIntent);
        context.startService(launchIntent);
    }
}
