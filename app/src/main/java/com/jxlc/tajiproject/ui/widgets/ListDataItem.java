package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jxlc.tajiproject.R;
import com.randal.aviana.DensityUtils;

/**
 * Created by randal on 2017/5/16.
 */

public class ListDataItem extends RelativeLayout {
    private Context mContext;
    private TextView mTextView;
    private EditText mEditText;
    private TextView mUnitView;

    private static final int TEXT_WIDTH = 60;         // dp
    private static final int TEXT_SIZE = 13;         // sp
    private static final int TEXT_COLOR = Color.parseColor("#404040");
    private static final int EDIT_MARGIN_LEFT = 16;         // dp
    private static final int EDIT_MARGIN_RIGHT = 2;         // dp
    private static final int UNIT_WIDTH = 18;         // dp

    private String title;
    private String unit;

    public ListDataItem(@NonNull Context context) {
        this(context, null);
    }

    public ListDataItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ListDataItem);
            title = a.getString(R.styleable.ListDataItem_dataitemtitle);
            unit = a.getString(R.styleable.ListDataItem_dataitemunit);
            a.recycle();
        }

        RelativeLayout.LayoutParams tlp = new LayoutParams(
                DensityUtils.dp2px(mContext, TEXT_WIDTH), ViewGroup.LayoutParams.WRAP_CONTENT);
        tlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mTextView = new TextView(mContext, attrs);
        mTextView.setText(title);
        mTextView.setTextColor(TEXT_COLOR);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        int textId = View.generateViewId();
        mTextView.setId(textId);
        addView(mTextView, tlp);

        RelativeLayout.LayoutParams ulp = new LayoutParams(
                DensityUtils.dp2px(mContext, UNIT_WIDTH), ViewGroup.LayoutParams.WRAP_CONTENT);
        ulp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ulp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mUnitView = new TextView(mContext, attrs);
        mUnitView.setText(unit);
        mUnitView.setTextColor(TEXT_COLOR);
        mUnitView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        int unitId = View.generateViewId();
        mUnitView.setId(unitId);
        addView(mUnitView, ulp);

        RelativeLayout.LayoutParams elp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        elp.addRule(RelativeLayout.RIGHT_OF, textId);
        elp.addRule(RelativeLayout.LEFT_OF, unitId);
        elp.setMargins(DensityUtils.dp2px(mContext, EDIT_MARGIN_LEFT), 0, DensityUtils.dp2px(mContext, EDIT_MARGIN_RIGHT), 0);
        mEditText = new EditText(mContext);
        mEditText.setTextColor(TEXT_COLOR);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        mEditText.setGravity(Gravity.CENTER);
        mEditText.setPadding(0, 0, 0, 0);
        int editId = View.generateViewId();
        mEditText.setId(editId);
        addView(mEditText, elp);
    }

    public void setTitle(String str) {
        mTextView.setText(str);
    }

    public void setContent(String str) {
        mEditText.setText(str);
    }
}
