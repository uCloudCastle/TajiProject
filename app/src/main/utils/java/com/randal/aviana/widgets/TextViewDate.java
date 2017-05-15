package com.randal.aviana.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;


public class TextViewDate extends TextView {
    private Context mContext;
    private String mFormat = "MM/dd kk:mm";

    private class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            dateUpdate();
        }
    }

    private TimeChangeReceiver mTimeChangeReceiver;

    public TextViewDate(Context context) {
        this(context, null);
    }

    public TextViewDate(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mTimeChangeReceiver = new TimeChangeReceiver();
        dateUpdate();
    }

    public void setFormat(String format) {
        mFormat = format;
    }

    private void dateUpdate() {
        long sysTime = System.currentTimeMillis();
        String sysTimeStr = (String) DateFormat.format(mFormat, sysTime);
        setText(sysTimeStr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mContext.registerReceiver(mTimeChangeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext.unregisterReceiver(mTimeChangeReceiver);
    }
}
