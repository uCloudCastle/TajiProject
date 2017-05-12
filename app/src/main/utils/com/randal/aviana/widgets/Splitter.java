package com.randal.aviana.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.randal.aviana.DensityUtils;

/**
 * Created by randal on 2017/5/10.
 */

public class Splitter extends View {
    Paint mPaint;
    int backgourdColor = Color.parseColor("#F5F5F5");
    int lineColor = Color.parseColor("#686868");
    int defaultWidth = 16;      // dp
    int defaultHeight = 16;     // dp
    int lineLength = 24;        // dp

    public Splitter(Context context) {
        this(context, null);
    }

    public Splitter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Splitter(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        defaultWidth = DensityUtils.dp2px(context, defaultWidth);
        defaultHeight = DensityUtils.dp2px(context, defaultHeight);
        lineLength = DensityUtils.dp2px(context, lineLength);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = defaultWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = defaultHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(backgourdColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

        mPaint.setColor(lineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        int widthD4 = getWidth() / 4;
        int heightD2 = getHeight() / 2;
        int lineD4 = lineLength / 4;
        canvas.drawLine(widthD4, heightD2 - lineD4, widthD4, heightD2 + lineD4, mPaint);
        canvas.drawLine(widthD4 * 2, heightD2 - lineD4 * 2, widthD4 * 2, heightD2 + lineD4 * 2, mPaint);
        canvas.drawLine(widthD4 * 3, heightD2 - lineD4, widthD4 * 3, heightD2 + lineD4, mPaint);
    }
}
