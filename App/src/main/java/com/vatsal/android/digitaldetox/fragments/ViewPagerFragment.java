package com.vatsal.android.digitaldetox.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vatsal.android.digitaldetox.R;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {

    @BindView(R.id.viewPager)
    protected ViewPager viewpager;
    @BindView(R.id.tabLayout)
    protected TabLayout tabLayout;
    @BindArray(R.array.viewpager_tab_titles)
    protected String[] tabTitles;

    private Unbinder unbinder;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("meh", "onCreateView: ");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        viewpager.setAdapter(new PagerAdaptor(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewpager);

        return rootView;
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private class PagerAdaptor extends FragmentPagerAdapter {


        PagerAdaptor(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = AppUsageStatisticsFragment.newInstance();
                    break;

                    default:
                        break;

            }

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
