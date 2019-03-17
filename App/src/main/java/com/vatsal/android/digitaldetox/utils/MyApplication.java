package com.vatsal.android.digitaldetox.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

@SuppressLint("Registered")
public class MyApplication extends Application {

    private static MyApplication sInstance=null;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }

}
