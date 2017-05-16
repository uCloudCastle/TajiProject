package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.jxlc.tajiproject.R;

/**
 * Created by randal on 2017/5/16.
 */

public class TowerDataItemList extends LinearLayout {
    private Context mContext;

    public TowerDataItemList(@NonNull Context context) {
        this(context, null);
    }

    public TowerDataItemList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widgets_towerdataitemlist, this, true);
    }
}
