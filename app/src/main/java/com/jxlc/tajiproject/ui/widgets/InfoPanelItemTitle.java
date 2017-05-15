package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.randal.aviana.widgets.Arrow;

/**
 * Created by randal on 2017/5/15.
 */

public class InfoPanelItemTitle extends LinearLayout {
    private Arrow mArrow;
    private TextView mTextView;

    public InfoPanelItemTitle(Context context) {
        this(context, null);
    }

    public InfoPanelItemTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        //LayoutInflater.from(context).inflate(R.layout.layout_siteinfopanel, this, true);
        setBackgroundColor(Color.parseColor("#193051"));

        mArrow = new Arrow(context);
        addView(mArrow);

        mTextView = new TextView(context);
        mTextView.setText("运行状态");
        addView(mTextView);
    }
}
