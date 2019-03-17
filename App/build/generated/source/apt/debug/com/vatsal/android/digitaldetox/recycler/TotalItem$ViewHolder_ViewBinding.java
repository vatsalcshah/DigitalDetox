// Generated code from Butter Knife. Do not modify!
package com.vatsal.android.digitaldetox.recycler;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.vatsal.android.digitaldetox.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TotalItem$ViewHolder_ViewBinding implements Unbinder {
  private TotalItem.ViewHolder target;

  @UiThread
  public TotalItem$ViewHolder_ViewBinding(TotalItem.ViewHolder target, View source) {
    this.target = target;

    target.mStartTime = Utils.findRequiredViewAsType(source, R.id.start_time, "field 'mStartTime'", TextView.class);
    target.mTotalTime = Utils.findRequiredViewAsType(source, R.id.total_time, "field 'mTotalTime'", TextView.class);
    target.mAppName = Utils.findRequiredViewAsType(source, R.id.textview_package_name, "field 'mAppName'", TextView.class);
    target.mAppIcon = Utils.findRequiredViewAsType(source, R.id.app_icon, "field 'mAppIcon'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    TotalItem.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mStartTime = null;
    target.mTotalTime = null;
    target.mAppName = null;
    target.mAppIcon = null;
  }
}
