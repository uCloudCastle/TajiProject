package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.jxlc.tajiproject.R;

/**
 * Created by Randal on 2017-05-16.
 */

public class DeveloperOptions extends LinearLayout {
    private Context mContext;

    public DeveloperOptions(@NonNull Context context) {
        this(context, null);
    }

    public DeveloperOptions(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widgets_developeroptions, this, true);
        mContext = context;

    }
}
