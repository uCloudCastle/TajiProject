package com.randal.aviana.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Randal on 2017-05-16.
 */

public class Round extends View {
    private Paint mPaint;

    private static final int DEFAULT_COLOR = Color.GREEN;
    private static final int DEFAULT_ITEM_WIDTH = 48;
    private static final int DEFAULT_ITEM_HEIGHT = 48 ;

    public Round(Context context) {
        this(context, null);
    }

    public Round(Context context, AttributeSet attrs) {
        this(context, attrs, DEFAULT_COLOR);
    }

    public Round(Context context, AttributeSet attrs, int color) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    public void setRoundColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float radius = width < height ? width : height;
        canvas.drawCircle(width / 2, height / 2, radius, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int width = 0;
        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            width = DEFAULT_ITEM_WIDTH;
        }
        return width;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            height = DEFAULT_ITEM_HEIGHT;
        }
        return height;
    }
}
