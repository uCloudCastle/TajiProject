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

public class DiagramLayout extends FrameLayout {
    private Context mContext;

    public DiagramLayout(@NonNull Context context) {
        this(context, null);
    }

    public DiagramLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_diagram, this, true);
        mContext = context;
    }
}
