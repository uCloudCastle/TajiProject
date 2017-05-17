package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jxlc.tajiproject.R;
import com.randal.aviana.DensityUtils;
import com.randal.aviana.widgets.Round;

/**
 * Created by randal on 2017/5/17.
 */

public class StatusIndicatorItem extends RelativeLayout {
    private TextView mTextView;
    private Round mIndicator;

    private static final int TEXT_WIDTH = 120;         // dp
    private static final int TEXT_SIZE = 13;         // sp
    private static final int TEXT_COLOR = Color.parseColor("#404040");
    private static final int ROUND_WIDTH = 20;         // dp
    private static final int ROUND_MARGIN_RIGHT = 6;         // dp
    private static final int ROUND_COLOR = Color.parseColor("#00CC00");

    private String title;

    public StatusIndicatorItem(@NonNull Context context) {
        this(context, null);
    }

    public StatusIndicatorItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StatusIndicatorItem);
            title = a.getString(R.styleable.StatusIndicatorItem_indicatortitle);
            a.recycle();
        }

        RelativeLayout.LayoutParams tlp = new LayoutParams(
                DensityUtils.dp2px(context, TEXT_WIDTH), ViewGroup.LayoutParams.WRAP_CONTENT);
        tlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mTextView = new TextView(context, attrs);
        mTextView.setText(title);
        mTextView.setTextColor(TEXT_COLOR);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        addView(mTextView, tlp);

        RelativeLayout.LayoutParams ilp = new LayoutParams(
                DensityUtils.dp2px(context, ROUND_WIDTH), DensityUtils.dp2px(context, ROUND_WIDTH));
        ilp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ilp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        ilp.setMargins(0, 0, DensityUtils.dp2px(context, ROUND_MARGIN_RIGHT), 0);
        mIndicator = new Round(context);
        mIndicator.setRoundColor(ROUND_COLOR);
        addView(mIndicator, ilp);
    }

    public void setIndicatorTitle(String str) {
        mTextView.setText(str);
    }

    public void setIndicatorColor(int color) {
        mIndicator.setRoundColor(color);
    }
}
