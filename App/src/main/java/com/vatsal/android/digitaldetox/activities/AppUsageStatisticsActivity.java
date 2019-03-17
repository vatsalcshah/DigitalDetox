package com.vatsal.android.digitaldetox.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.vatsal.android.digitaldetox.R;
import com.vatsal.android.digitaldetox.fragments.AppDetailFragment;
import com.vatsal.android.digitaldetox.fragments.AppUsageStatisticsFragment;
import com.vatsal.android.digitaldetox.fragments.MostAppUsageStatisticsFragment;
import com.vatsal.android.digitaldetox.services.PopulateDatabaseService;

import java.util.Objects;


public class AppUsageStatisticsActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage_statistics);

        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Most Used Apps");
        getSupportActionBar().setBackgroundDrawable();


        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.notify:
                        Toast.makeText(AppUsageStatisticsActivity.this, "App Usage Alerts",Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(AppUsageStatisticsActivity.this, NotificationActivity.class);
                        AppUsageStatisticsActivity.this.startActivity(myIntent);
                        break;
                    case R.id.about_us:
                        Toast.makeText(AppUsageStatisticsActivity.this, "About Us",Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });


            Toast.makeText(this,"Welcome",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PopulateDatabaseService.class);
            stopService(intent);
            startService(intent);

            Fragment fragment1 = new MostAppUsageStatisticsFragment();
            Fragment fragment2 = new AppUsageStatisticsFragment();

            FragmentManager fragmentManager = AppUsageStatisticsActivity.this.getSupportFragmentManager();

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container1,fragment1);
            transaction.replace(R.id.container2,fragment2);
            transaction.addToBackStack(null);
            transaction.commit();

    }
    private void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment instanceof AppDetailFragment)
            transaction.addToBackStack(null);

        transaction.replace(R.id.container2, fragment)
                .commit();
    }
    public void showDetail(String appName, int dateOffset) {
        changeFragment(AppDetailFragment.newInstance(appName, dateOffset));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }
}
