package com.vatsal.android.digitaldetox.fragments;


import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.vatsal.android.digitaldetox.R;
import com.vatsal.android.digitaldetox.adapters.MostUsageListAdapter;
import com.vatsal.android.digitaldetox.receiver.CustomUsageStats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class MostAppUsageStatisticsFragment extends Fragment {

    private static final String TAG = MostAppUsageStatisticsFragment.class.getSimpleName();

    //VisibleForTesting for variables below
    UsageStatsManager mUsageStatsManager;
    MostUsageListAdapter mMostUsageListAdapter;
    RecyclerView mRecyclerView;
    Button mOpenUsageSettingButton;
    Spinner mSpinnerTimeSpan;
    Spinner mSpinnerSort;
    List<UsageStats> usageStatsList;
    RecyclerView.LayoutManager mLayoutManager;
    int n;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link MostAppUsageStatisticsFragment}.
     */
    public static MostAppUsageStatisticsFragment newInstance() {
        MostAppUsageStatisticsFragment fragment = new MostAppUsageStatisticsFragment();
        return fragment;
    }

    public MostAppUsageStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService(Context.USAGE_STATS_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_most_usage_statistics, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        mMostUsageListAdapter = new MostUsageListAdapter(getContext());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_app_usage);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mLayoutManager = mRecyclerView.getLayoutManager();
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mMostUsageListAdapter);
        mOpenUsageSettingButton = (Button) rootView.findViewById(R.id.button_open_usage_setting);

        mSpinnerTimeSpan = (Spinner) rootView.findViewById(R.id.spinner_time_span);


    }


    @Override
    public void onResume() {
        super.onResume();

        setmSpinnerTimeSpanAdapter();


    }

public void setmSpinnerTimeSpanAdapter()
{
    SpinnerAdapter timespan_spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
            R.array.action_timespan, android.R.layout.simple_spinner_dropdown_item);
    mSpinnerTimeSpan.setAdapter(timespan_spinnerAdapter);
    mSpinnerTimeSpan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] strings = getResources().getStringArray(R.array.action_timespan);
            StatsUsageInterval statsUsageInterval = StatsUsageInterval
                    .getValue(strings[position]);
            if (statsUsageInterval != null) {
                usageStatsList = getUsageStatistics(statsUsageInterval.mInterval);
                Collections.sort(usageStatsList, new timeInForegroundComparator());
                updateAppsList(usageStatsList);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    });
}

    class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private int position;
        ProgressDialog progressDialog;
         AsyncTaskRunner(int pos)
         {
          this.position=pos;
         }
        @Override
        protected String doInBackground(String... params) {
            String[] strings = getResources().getStringArray(R.array.action_timespan);
            StatsUsageInterval statsUsageInterval = StatsUsageInterval
                    .getValue(strings[position]);
            if (statsUsageInterval != null) {
                List<UsageStats> usageStatsList =
                        getUsageStatistics(statsUsageInterval.mInterval);
                Collections.sort(usageStatsList, new timeInForegroundComparator());
                updateAppsList(usageStatsList);
            }
            return position+"";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();

        }
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(),
                    "Loading",null);
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }

    }


    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        if(intervalType==UsageStatsManager.INTERVAL_DAILY) {
            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
        }
        else if(intervalType==UsageStatsManager.INTERVAL_WEEKLY)
            cal.add(Calendar.DATE, -7);
        else if(intervalType==UsageStatsManager.INTERVAL_MONTHLY)
            cal.add(Calendar.MONTH, -1);
        else if(intervalType==UsageStatsManager.INTERVAL_YEARLY)
            cal.add(Calendar.YEAR, -1);

        Map<String,UsageStats> queryUsageStatsMap = mUsageStatsManager
                .queryAndAggregateUsageStats( cal.getTimeInMillis(),
                        System.currentTimeMillis());
        List<UsageStats> queryUsageStats=new ArrayList<UsageStats>() ;
        for(Map.Entry<String,UsageStats> stat: queryUsageStatsMap.entrySet())
        {
            queryUsageStats.add(stat.getValue());
        }

        if (queryUsageStats.size() == 0) {
            Log.i(TAG, "The user may not allow the access to apps usage. ");
            Toast.makeText(getActivity(),
                    getString(R.string.explanation_access_to_appusage_is_not_enabled),
                    Toast.LENGTH_LONG).show();
            mOpenUsageSettingButton.setVisibility(View.VISIBLE);
            mOpenUsageSettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
        }
        return queryUsageStats;
    }


    void updateAppsList(List<UsageStats> usageStatsList) {

        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) getContext()
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (getContext().checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        if(!granted){
            n = usageStatsList.size();
        }else {n=5;
        }

        List<CustomUsageStats> customUsageStatsList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            CustomUsageStats customUsageStats = new CustomUsageStats();
            customUsageStats.usageStats = usageStatsList.get(i);
            try {
                Drawable appIcon = getActivity().getPackageManager()
                        .getApplicationIcon(customUsageStats.usageStats.getPackageName());
                customUsageStats.appIcon = appIcon;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, String.format("App Icon is not found for %s",
                        customUsageStats.usageStats.getPackageName()));
                customUsageStats.appIcon = getActivity()
                        .getDrawable(R.drawable.ic_launcher);
            }
            customUsageStatsList.add(customUsageStats);
        }
        mMostUsageListAdapter.setCustomUsageStatsList(customUsageStatsList);
        mMostUsageListAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }


    private  class timeInForegroundComparator implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getTotalTimeInForeground(), left.getTotalTimeInForeground());
        }
    }
    private  class AlphabeticComparator implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {
            PackageManager pm= getActivity().getPackageManager();
            ApplicationInfo ai_left=null;
            ApplicationInfo ai_right=null;
            try {
                ai_left=pm.getApplicationInfo(left.getPackageName(), 0);
                ai_right=pm.getApplicationInfo(right.getPackageName(), 0);

            }catch (final PackageManager.NameNotFoundException e) {
                ai_left = null;
                ai_right = null;
            }
            final String applicationNameLeft = (String) (ai_left != null ? pm.getApplicationLabel(ai_left) : "(unknown)");
            final String applicationNameRight = (String) (ai_right != null ? pm.getApplicationLabel(ai_right) : "(unknown)");
           return applicationNameLeft.compareTo(applicationNameRight);

        }
    }

    static enum StatsUsageInterval {
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static StatsUsageInterval getValue(String stringRepresentation) {
            for (StatsUsageInterval statsUsageInterval : values()) {
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }

}
