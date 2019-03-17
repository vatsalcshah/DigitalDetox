// Generated code from Butter Knife. Do not modify!
package com.vatsal.android.digitaldetox.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.vatsal.android.digitaldetox.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AppUsageStatisticsFragment_ViewBinding implements Unbinder {
  private AppUsageStatisticsFragment target;

  @UiThread
  public AppUsageStatisticsFragment_ViewBinding(AppUsageStatisticsFragment target, View source) {
    this.target = target;

    target.mRecyclerView = Utils.findRequiredViewAsType(source, R.id.recyclerview_app_usage, "field 'mRecyclerView'", RecyclerView.class);
    target.headerUsage = Utils.findRequiredViewAsType(source, R.id.header_usage, "field 'headerUsage'", TextView.class);
    target.dateNext = Utils.findRequiredViewAsType(source, R.id.date_next, "field 'dateNext'", Button.class);
    target.datePrev = Utils.findRequiredViewAsType(source, R.id.date_previous, "field 'datePrev'", Button.class);
    target.dateText = Utils.findRequiredViewAsType(source, R.id.date_text, "field 'dateText'", TextView.class);
    target.dateLayout = Utils.findRequiredViewAsType(source, R.id.date_layout, "field 'dateLayout'", RelativeLayout.class);
    target.floatingDate = Utils.findRequiredViewAsType(source, R.id.floating_date_text, "field 'floatingDate'", TextView.class);

    Context context = source.getContext();
    Resources res = context.getResources();
    target.excludePackages = res.getStringArray(R.array.exclude_packages);
  }

  @Override
  @CallSuper
  public void unbind() {
    AppUsageStatisticsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mRecyclerView = null;
    target.headerUsage = null;
    target.dateNext = null;
    target.datePrev = null;
    target.dateText = null;
    target.dateLayout = null;
    target.floatingDate = null;
  }
}
