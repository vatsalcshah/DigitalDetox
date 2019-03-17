// Generated code from Butter Knife. Do not modify!
package com.vatsal.android.digitaldetox.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.vatsal.android.digitaldetox.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AppDetailFragment_ViewBinding implements Unbinder {
  private AppDetailFragment target;

  @UiThread
  public AppDetailFragment_ViewBinding(AppDetailFragment target, View source) {
    this.target = target;

    target.mRecyclerView = Utils.findRequiredViewAsType(source, R.id.recyclerview_app_detail, "field 'mRecyclerView'", RecyclerView.class);
    target.mChart = Utils.findRequiredViewAsType(source, R.id.detail_chart, "field 'mChart'", PieChart.class);
    target.noUsageTV = Utils.findRequiredViewAsType(source, R.id.detail_no_usage, "field 'noUsageTV'", TextView.class);
    target.noUsageChartTV = Utils.findRequiredViewAsType(source, R.id.detail_chart_no_usage, "field 'noUsageChartTV'", TextView.class);
    target.appBarLayout = Utils.findRequiredViewAsType(source, R.id.appbar, "field 'appBarLayout'", AppBarLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AppDetailFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mRecyclerView = null;
    target.mChart = null;
    target.noUsageTV = null;
    target.noUsageChartTV = null;
    target.appBarLayout = null;
  }
}
