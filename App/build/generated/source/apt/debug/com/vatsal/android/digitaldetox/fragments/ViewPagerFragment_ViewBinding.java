// Generated code from Butter Knife. Do not modify!
package com.vatsal.android.digitaldetox.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.vatsal.android.digitaldetox.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ViewPagerFragment_ViewBinding implements Unbinder {
  private ViewPagerFragment target;

  @UiThread
  public ViewPagerFragment_ViewBinding(ViewPagerFragment target, View source) {
    this.target = target;

    target.viewpager = Utils.findRequiredViewAsType(source, R.id.viewPager, "field 'viewpager'", ViewPager.class);
    target.tabLayout = Utils.findRequiredViewAsType(source, R.id.tabLayout, "field 'tabLayout'", TabLayout.class);

    Context context = source.getContext();
    Resources res = context.getResources();
    target.tabTitles = res.getStringArray(R.array.viewpager_tab_titles);
  }

  @Override
  @CallSuper
  public void unbind() {
    ViewPagerFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.viewpager = null;
    target.tabLayout = null;
  }
}
