package com.vatsal.android.digitaldetox.adapters;

import com.vatsal.android.digitaldetox.models.DisplayEventEntity;
import com.vatsal.android.digitaldetox.recycler.TotalItem;
import com.mikepenz.fastadapter.AbstractAdapter;
import com.turingtechnologies.materialscrollbar.IDateableAdapter;

import java.util.Date;
import java.util.List;

/**
 * Created by j on 21/7/17.
 */

public class ScrollAdapter extends AbstractAdapter<TotalItem> implements IDateableAdapter {
    @Override
    public Date getDateForElement(int element) {
        TotalItem totalItem = getItem(element);
        DisplayEventEntity model = null;
        if (totalItem != null)
            model = totalItem.getModel();
        if (model != null)
            return new Date(model.startTime);
        return new Date();
    }

    @Override
    public TotalItem getAdapterItem(int position) {
        return null;
    }

    @Override
    public List<TotalItem> getAdapterItems() {
        return null;
    }

    @Override
    public int getAdapterPosition(long identifier) {
        return -1;
    }

    @Override
    public int getAdapterPosition(TotalItem item) {
        return -1;
    }

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public int getGlobalPosition(int position) {
        return -1;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }
}
