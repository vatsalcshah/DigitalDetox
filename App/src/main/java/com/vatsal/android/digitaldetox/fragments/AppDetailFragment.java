package com.vatsal.android.digitaldetox.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vatsal.android.digitaldetox.R;
import com.vatsal.android.digitaldetox.adapters.ScrollAdapter;
import com.vatsal.android.digitaldetox.models.AppFilteredEvents;
import com.vatsal.android.digitaldetox.models.DisplayEventEntity;
import com.vatsal.android.digitaldetox.recycler.TotalItem;
import com.vatsal.android.digitaldetox.utils.FormatEventsViewModel;
import com.vatsal.android.digitaldetox.utils.Tools;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.turingtechnologies.materialscrollbar.DateAndTimeIndicator;
import com.turingtechnologies.materialscrollbar.TouchScrollBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AppDetailFragment extends Fragment {

    private static final String KEY_APP_NAME = "appName";
    private static final String KEY_DATE_OFFSET = "dateOffset";

    @BindView(R.id.recyclerview_app_detail)
    RecyclerView mRecyclerView;
    @BindView(R.id.detail_chart)
    PieChart mChart;
    @BindView(R.id.detail_no_usage)
    TextView noUsageTV;
    @BindView(R.id.detail_chart_no_usage)
    TextView noUsageChartTV;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    private Unbinder mUnbinder;
    private FormatEventsViewModel formatCustomUsageEvents;
    private String mAppName;
    private int mDateOffset;

    public static AppDetailFragment newInstance(String appName, int dateOffset) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_APP_NAME, appName);
        bundle.putInt(KEY_DATE_OFFSET, dateOffset);
        AppDetailFragment fragment = new AppDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mAppName = bundle.getString(KEY_APP_NAME);
            mDateOffset = bundle.getInt(KEY_DATE_OFFSET);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_detail, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.scrollToPosition(0);

        FastAdapter<TotalItem> mFastAdapter = new FastAdapter<>();
        GenericItemAdapter<DisplayEventEntity, TotalItem> mTotalAdapter =
                new GenericItemAdapter<>(TotalItem.class, DisplayEventEntity.class);
        ScrollAdapter scrollAdapter = new ScrollAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext()));
        mRecyclerView.setItemAnimator(null);

        mRecyclerView.setAdapter(scrollAdapter.wrap(mTotalAdapter.wrap(mFastAdapter)));

        TouchScrollBar materialScrollBar = new TouchScrollBar(getActivity().getApplicationContext(),
                mRecyclerView, true);
        materialScrollBar.setHandleColourRes(R.color.colorAccent);
        materialScrollBar.setBarColourRes(R.color.scrollbarBgGray);
        materialScrollBar.addIndicator(new DateAndTimeIndicator(getActivity().
                getApplicationContext(), false, false, false, true), true);

        formatCustomUsageEvents = ViewModelProviders
                .of(this)
                .get(FormatEventsViewModel.class);

        formatCustomUsageEvents
                .getAppDetailEventsList()
                .observe(this, allEvents -> {
                    assert allEvents != null;
                    AppFilteredEvents appFilteredEvents = Tools.getSpecificAppEvents(allEvents, mAppName);
                    if (appFilteredEvents.appEvents == null || appFilteredEvents.appEvents.size() == 0) {
                        mTotalAdapter.clear();
                        noUsageTV.setVisibility(View.VISIBLE);
                        noUsageChartTV.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                        return;
                    } else {
                        noUsageTV.setVisibility(View.GONE);
                        noUsageChartTV.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }

                    setPie(appFilteredEvents);

                    if (mTotalAdapter.getItem(0) != null) {
                        int index = findItemInList(appFilteredEvents.appEvents, mTotalAdapter.getItem(1).getModel());
                        if (index > -1) {
                            mTotalAdapter.removeModel(0);
                        }
                        for (int i = index - 1; i >= 0; i--) {
                            mTotalAdapter.addModel(0, appFilteredEvents.appEvents.get(i));
                        }
                    } else {
                        mTotalAdapter.clear();
                        mTotalAdapter.addModel(appFilteredEvents.appEvents);
                    }
                });

        triggerEvents();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    private int findItemInList(List<DisplayEventEntity> list, DisplayEventEntity event) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).appName.equals(event.appName) && list.get(i).startTime == event.startTime)
                return i;
        }
        return -1;
    }

    private void triggerEvents() {
        Calendar startCalender = Calendar.getInstance();
        if (mDateOffset < 0)
            startCalender.add(Calendar.DATE, mDateOffset);
        startCalender.set(Calendar.HOUR_OF_DAY, 0);
        startCalender.set(Calendar.MINUTE, 0);
        startCalender.set(Calendar.SECOND, 0);
        startCalender.set(Calendar.MILLISECOND, 0);


        Calendar endCalendar = Calendar.getInstance();
        if (mDateOffset < 0)
            endCalendar.add(Calendar.DATE, mDateOffset);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 999);

        formatCustomUsageEvents
                .setCachedEventsList(startCalender.getTimeInMillis(),
                        endCalendar.getTimeInMillis());
    }

    private void setPie(AppFilteredEvents appFilteredEvents) {
        Calendar elapsedTodayCalendar = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        elapsedTodayCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        endTime.setTimeZone(TimeZone.getTimeZone("UTC"));
        elapsedTodayCalendar.setTimeInMillis(elapsedTodayCalendar.getTimeInMillis()
                + TimeZone.getDefault().getOffset(elapsedTodayCalendar.getTimeInMillis()));
        endTime.setTimeInMillis(appFilteredEvents.endTime
                + TimeZone.getDefault().getOffset(endTime.getTimeInMillis()));
        long adjustedPeriodEndTime;

        if (endTime.get(Calendar.YEAR) < elapsedTodayCalendar.get(Calendar.YEAR)
                || endTime.get(Calendar.DAY_OF_YEAR) < elapsedTodayCalendar.get(Calendar.DAY_OF_YEAR)) {

            Calendar startTime = Calendar.getInstance();
            startTime.setTimeZone(TimeZone.getTimeZone("UTC"));
            startTime.setTimeInMillis(appFilteredEvents.startTime
                    + TimeZone.getDefault().getOffset(startTime.getTimeInMillis()));

            int offset = endTime.get(Calendar.DAY_OF_YEAR) - startTime.get(Calendar.DAY_OF_YEAR);

            endTime.set(Calendar.YEAR, 1970);
            endTime.set(Calendar.DAY_OF_YEAR, offset + 1);
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 59);
            endTime.set(Calendar.SECOND, 59);
            endTime.set(Calendar.MILLISECOND, 999);
            adjustedPeriodEndTime = endTime.getTimeInMillis();
        } else {
            elapsedTodayCalendar.set(Calendar.YEAR, 1970);
            elapsedTodayCalendar.set(Calendar.DAY_OF_YEAR, 1);
            adjustedPeriodEndTime = elapsedTodayCalendar.getTimeInMillis();
        }

        final double TIME_DAY = adjustedPeriodEndTime / 1000;
        final double TIME_USED_OTHERS = Tools.findTotalUsage(appFilteredEvents.otherEvents) / 1000;
        final double TIME_USED_THIS = Tools.findTotalUsage(appFilteredEvents.appEvents) / 1000;

        float otherPercent = (float) (TIME_USED_OTHERS / TIME_DAY * 100);
        float dayRemainingPercent = (float) ((TIME_DAY - TIME_USED_OTHERS - TIME_USED_THIS) / TIME_DAY * 100);
        float thisPercent = (float) (TIME_USED_THIS / TIME_DAY * 100);

        // Anything less than 1 tends to be invisible on the chart
        if (otherPercent < 1)
            otherPercent = 1;
        if (thisPercent < 1)
            thisPercent = 1;

        long totalUsage = Tools.findTotalUsage(appFilteredEvents.appEvents);
        String formattedTime = Tools.formatTotalTime(0, totalUsage, true);


        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(otherPercent, getResources().getString(R.string.detail_other_apps)));
        entries.add(new PieEntry(dayRemainingPercent, getResources().getString(R.string.detail_unused)));
        entries.add(new PieEntry(thisPercent, getResources().getString(R.string.detail_this_app)));

        PieDataSet dataSet = new PieDataSet(entries, "App usage");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(6f);
        dataSet.setColors(Tools.getColours(entries.size()));
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setDrawValues(false);

        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()).getApplicationContext(), R.color.textWhite));
        mChart.setCenterText(generateCenterSpannableText(mAppName, formattedTime));
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(64f);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setEntryLabelColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.textBlack));
        float finalOtherPercent = otherPercent;
        float finalThisPercent = thisPercent;
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                if (entry == null)
                    return;

                float label = entry.getY();
                if (label == dayRemainingPercent) {
                    String formattedTime = Tools.formatTotalTime(0, (long) ((TIME_DAY - TIME_USED_OTHERS - TIME_USED_THIS) * 1000), true);
                    mChart.setCenterText(generateCenterSpannableText(
                            getString(R.string.detail_unused), formattedTime));
                } else if (label == finalOtherPercent) {
                    String formattedTime = Tools.formatTotalTime(0, (long) (TIME_USED_OTHERS * 1000), true);
                    mChart.setCenterText(generateCenterSpannableText(
                            getString(R.string.detail_other_apps), formattedTime));
                } else if (label == finalThisPercent) {
                    String formattedTime = Tools.formatTotalTime(0, (long) (TIME_USED_THIS * 1000), true);
                    mChart.setCenterText(generateCenterSpannableText(mAppName, formattedTime));
                }
            }

            @Override
            public void onNothingSelected() {
                String formattedTime = Tools.formatTotalTime(0, (long) (TIME_USED_THIS * 1000), true);
                mChart.setCenterText(generateCenterSpannableText(mAppName, formattedTime));
            }
        });

        PieData data = new PieData(dataSet);

        Legend l = mChart.getLegend();
        l.setEnabled(false);

        mChart.setData(data);
        mChart.setVisibility(View.VISIBLE);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private SpannableString generateCenterSpannableText(String name, String formattedTime) {

        SpannableString s = new SpannableString(name + "\n\n" + ((formattedTime == null) ?
                getResources().getString(R.string.no_usage) : formattedTime));

        s.setSpan(new RelativeSizeSpan(0.9f), 0, name.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), name.length(), s.length(), 0);
        s.setSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(Objects.requireNonNull(getActivity()).getApplicationContext(), R.color.textBlack)),
                0, name.length(), 0);
        s.setSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(getActivity().getApplicationContext(), R.color.textDarkGray)),
                name.length() + 1, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(0.8f), name.length() + 1, s.length(), 0);
        return s;
    }

}
