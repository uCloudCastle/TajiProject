package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jxlc.tajiproject.R;
import com.randal.aviana.DensityUtils;
import com.randal.aviana.widgets.Arrow;

/**
 * Created by randal on 2017/5/15.
 */

public class InfoPanelItemTitle extends RelativeLayout {
    private Context mContext;
    private Arrow mArrow;
    private TextView mTextView;

    public static final int ARROW_LEFT = 0x10;
    public static final int ARROW_UP = 0x11;
    public static final int ARROW_RIGHT = 0x12;
    public static final int ARROW_DOWN = 0x13;

    private static final int ARROW_HEIGHT = 18;      // dp
    private static final int ARROW_MARGIN = 12;         // dp
    private static final float ARROW_PAINT_WIDTH = 1.5f;      // dp
    private static final int ARROW_COLOR = Color.parseColor("#121A2A");
    private static final int TEXT_SIZE = 18;         // sp
    private static final int LINE_WIDTH = 2;         // dp
    private static final int LINE_MARGIN = 8;         // dp
    private static final int TEXT_COLOR = Color.parseColor("#6699FF");

    private String title;

    public InfoPanelItemTitle(Context context) {
        this(context, null);
    }

    public InfoPanelItemTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.InfoPanelItemTitle);
            title = a.getString(R.styleable.InfoPanelItemTitle_title);
            a.recycle();
        }

        RelativeLayout.LayoutParams alp = new LayoutParams(
                DensityUtils.dp2px(mContext, ARROW_HEIGHT), DensityUtils.dp2px(mContext, ARROW_HEIGHT));
        alp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        alp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        alp.setMargins(DensityUtils.dp2px(mContext, ARROW_MARGIN), 0, DensityUtils.dp2px(mContext, ARROW_MARGIN), 0);
        mArrow = new Arrow(context, null, ARROW_COLOR, DensityUtils.dp2px(mContext, ARROW_PAINT_WIDTH), Arrow.ARROW_DOWN);
        mArrow.setIsFillArrow(false);
        int arrowId = View.generateViewId();
        mArrow.setId(arrowId);
        addView(mArrow, alp);

        RelativeLayout.LayoutParams tlp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tlp.addRule(RelativeLayout.RIGHT_OF, arrowId);
        tlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mTextView = new TextView(context);
        mTextView.setText(title);
        mTextView.setTextColor(TEXT_COLOR);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        int textId = View.generateViewId();
        mTextView.setId(textId);
        addView(mTextView, tlp);

        RelativeLayout.LayoutParams linelp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(mContext, LINE_WIDTH));
        linelp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linelp.setMargins(DensityUtils.dp2px(mContext, LINE_MARGIN), 0, DensityUtils.dp2px(mContext, LINE_MARGIN), 0);
        View line = new View(context);
        line.setBackgroundColor(TEXT_COLOR);
        addView(line, linelp);
    }

    public void setDirection(int d) {
        mArrow.setDirection(d);
    }
}
