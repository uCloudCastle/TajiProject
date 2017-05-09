package com.randal.aviana.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;


public class ScrollViewListened extends ScrollView {
    private OnScrollListener mListener;


    public ScrollViewListened(Context context) {
        super(context);
    }

    public ScrollViewListened(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewListened(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mListener == null) {
            return;
        }
        mListener.onScrollChanged(l, t, oldl, oldt);

        if (t <= 0 && oldt > 0) {
            mListener.onTopArrived();
        } else if (t > 0 && oldt <= 0) {
            mListener.onTopLeave();
        } else if (t + getHeight() >= computeVerticalScrollRange()) {
            mListener.onBottomArrived();
        } else if (oldt + getHeight() >= computeVerticalScrollRange() && oldt > t) {
            mListener.onBottomLeave();
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mListener = onScrollListener;
    }

    public interface OnScrollListener {

        void onBottomArrived();

        void onBottomLeave();

        void onTopArrived();

        void onTopLeave();

        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
