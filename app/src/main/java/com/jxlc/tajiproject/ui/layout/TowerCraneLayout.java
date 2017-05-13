package com.jxlc.tajiproject.ui.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.jxlc.tajiproject.R;

/**
 * Created by randal on 2017/5/4.
 */

public class TowerCraneLayout extends FrameLayout {
    private Context mContext;

    public TowerCraneLayout(@NonNull Context context) {
        this(context, null);
    }

    public TowerCraneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_towercrane, this, true);
    }
}
