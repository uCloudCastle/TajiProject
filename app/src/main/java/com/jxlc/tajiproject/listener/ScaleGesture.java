package com.jxlc.tajiproject.listener;

import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

/**
 * Created by randal on 2017/5/12.
 */

public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    private FrameLayout mLayout;
    private float lastScale = 1;

    public ScaleGesture(FrameLayout layout) {
        mLayout = layout;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        mLayout.setScaleX(lastScale * detector.getScaleFactor());
        mLayout.setScaleY(lastScale * detector.getScaleFactor());
        return super.onScale(detector);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return super.onScaleBegin(detector);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        lastScale = lastScale * detector.getScaleFactor();
        super.onScaleEnd(detector);
    }
}
