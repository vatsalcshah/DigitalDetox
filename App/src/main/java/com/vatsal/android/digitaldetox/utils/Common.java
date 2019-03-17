package com.vatsal.android.digitaldetox.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import com.vatsal.android.digitaldetox.R;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Common {


    private static ProgressDialog progressDialog;



    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        try {
            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            progressDialog = null;
        }
    }


    private static final List<Long> times = Arrays.asList(TimeUnit.DAYS.toMillis(365), TimeUnit.DAYS.toMillis(30), TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(1), TimeUnit.MINUTES.toMillis(1), TimeUnit.SECONDS.toMillis(1));
    private static final List<String> timesString = Arrays.asList("year", "month", "day", "hr", "min", "sec");

    public static String getFormattedTime(long duration) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(timesString.get(i)).append(temp > 1 ? "s" : "");
                break;
            }
        }
        if ("".equals(res.toString())) return "Just now";
        else return res.toString();
    }

    public static String getAppName(String packageName) {
        PackageManager packageManager = MyApplication.getAppContext().getPackageManager();
        try {
            return (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }

}
