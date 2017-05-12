package com.jxlc.tajiproject.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by randal on 2017/5/12.
 */

public class ScrollTapGesture extends GestureDetector.SimpleOnGestureListener {
    private FrameLayout mLayout;
    private float lastTranslationX = 0;
    private float lastTranslationY = 0;

    public ScrollTapGesture(FrameLayout layout) {
        mLayout = layout;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mLayout.setScaleX(1);
        mLayout.setScaleY(1);
        mLayout.setTranslationX(0);
        mLayout.setTranslationY(0);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        lastTranslationX = lastTranslationX - distanceX;
        lastTranslationY = lastTranslationY - distanceY;
        mLayout.setTranslationX(lastTranslationX);
        mLayout.setTranslationY(lastTranslationY);

        return super.onScroll(e1, e2, distanceX, distanceY);
    }
}
