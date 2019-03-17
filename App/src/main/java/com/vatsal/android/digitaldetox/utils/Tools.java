package com.vatsal.android.digitaldetox.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;

import com.vatsal.android.digitaldetox.models.AppFilteredEvents;
import com.vatsal.android.digitaldetox.models.DisplayEventEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class Tools {
    public static DisplaySize getDisplaySizes(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new DisplaySize(size.x, size.y);
    }

    public static String findLauncher(Context context) {
        PackageManager localPackageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        return localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatTotalTime(long startMillis, long endMillis, boolean needSeconds) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfSpaces = new SimpleDateFormat("H m s");
        SimpleDateFormat sdfSingular;
        if (needSeconds)
            sdfSingular = new SimpleDateFormat("'$'H 'hour,' '$'m 'minute, and' '$'s 'second'");
        else
            sdfSingular = new SimpleDateFormat("'$'H 'hour, and' '$'m 'minute'");
        Date date = new Date(endMillis - startMillis);

        sdfSingular.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdfSpaces.setTimeZone(TimeZone.getTimeZone("UTC"));

        String[] formattedSpaces = sdfSpaces.format(date).split(" ");

        int i = 0;
        byte pluralH = 0;
        byte pluralM = 0;
        byte pluralS = 0;
        for (String current : formattedSpaces) {
            switch (i++) {
                case 0:
                    int elementH = Integer.parseInt(current);
                    if (elementH > 1)
                        pluralH = 1;
                    else if (elementH == 0)
                        pluralH = 2;
                    break;
                case 1:
                    int elementM = Integer.parseInt(current);
                    if (elementM > 1)
                        pluralM = 1;
                    else if (elementM == 0)
                        pluralM = 2;
                    break;
                case 2:
                    int elementS = Integer.parseInt(current);
                    if (elementS > 1)
                        pluralS = 1;
                    else if (elementS == 0)
                        pluralS = 2;
                    break;
            }
        }

        String formatted = sdfSingular.format(date);

        if (pluralH == 1)
            formatted = formatted.replace("hour", "hours");
        else if (pluralH == 2)
            formatted = formatted.replace("hour, ", "");
        if (pluralM == 1)
            formatted = formatted.replace("minute", "minutes");
        else if (pluralM == 2)
            formatted = formatted.replace("minute, ", "");
        if (pluralS == 1)
            formatted = formatted.replace("second", "seconds");
        else if (pluralS == 2) {
            formatted = formatted.replace("second", "");
            formatted = formatted.replace(", and", "");
        }

        if (!needSeconds && pluralH == 2) {
            if (pluralM == 2)
                formatted = null;
            else
                formatted = formatted.replace("and", "");
        } else if (needSeconds && pluralH == 2 && pluralM == 2) {
            if (pluralS == 2)
                formatted = null;
            else
                formatted = formatted.replace("and", "");
        }

        if (formatted != null) {
            formatted = formatted.replace("$0", "");
            formatted = formatted.replace("$", "");
        }

        return formatted;
    }

    public static AppFilteredEvents getSpecificAppEvents(List<DisplayEventEntity> allEvents, String appName) {
        AppFilteredEvents appFilteredEvents = new AppFilteredEvents();
        appFilteredEvents.startTime = allEvents.get(allEvents.size() - 1).startTime;
        appFilteredEvents.endTime = allEvents.get(0).endTime;
        for (DisplayEventEntity event : allEvents) {
            if (appName.equals(event.appName))
                appFilteredEvents.appEvents.add(event);
            else
                appFilteredEvents.otherEvents.add(event);
        }
        return appFilteredEvents;
    }

    public static long findTotalUsage(List<DisplayEventEntity> events) {
        long totalUsage = 0;
        for (DisplayEventEntity event : events) {
            if (event.endTime == 0)
                continue;
            totalUsage += event.endTime - event.startTime;
        }
        return totalUsage;
    }

    public static ArrayList<Integer> getColours(int count) {
        final int[] LIBERTY_COLORS = new int[]{Color.rgb(207, 248, 246), Color.rgb(148, 212, 212), Color.rgb(136, 180, 187), Color.rgb(118, 174, 175), Color.rgb(42, 109, 130)};
        final int[] JOYFUL_COLORS = new int[]{Color.rgb(217, 80, 138), Color.rgb(254, 149, 7), Color.rgb(254, 247, 120), Color.rgb(106, 167, 134), Color.rgb(53, 194, 209)};
        //final int[] PASTEL_COLORS = new int[]{Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162), Color.rgb(191, 134, 134), Color.rgb(179, 48, 80)};
        final int[] COLORFUL_COLORS = new int[]{Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0), Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)};
        final int[] VORDIPLOM_COLORS = new int[]{Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140), Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)};

        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<Integer> finalColors = new ArrayList<>();
        for (int c : VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : JOYFUL_COLORS)
            colors.add(c);
        for (int c : COLORFUL_COLORS)
            colors.add(c);
        for (int c : LIBERTY_COLORS)
            colors.add(c);
        /*for (int c : PASTEL_COLORS)
            colors.add(c);*/

        if (colors.size() < count)
            return colors;

        for (int i = 0; i <= count; i++) {
            finalColors.add(colors.get((int) (Math.random() * colors.size())));
        }
        return finalColors;
    }
}
