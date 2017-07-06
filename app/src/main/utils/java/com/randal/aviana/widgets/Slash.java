package com.randal.aviana.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.jxlc.tajiproject.R;


/**
 * Created by randal on 2017/7/6.
 */

public class Slash extends View {
    private Paint mPaint;
    public static final int SWING_RIGHT = 0;
    public static final int SWING_LEFT = 1;

    private static final int DEFAULT_ITEM_WIDTH = 25;
    private static final int DEFAULT_ITEM_HEIGHT = 50;

    private static final int DEFAULT_LINE_COLOR = Color.WHITE;
    private static final int DEFAULT_LINE_WIDTH = 2;
    private static final int DEFAULT_DIRECTION = SWING_RIGHT;

    private int mLineColor;
    private int mLineWidth;
    private int mDirection;

    public Slash(Context context) {
        this(context, null);
    }

    public Slash(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Slash(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAttributeSet(attrs);

        mPaint = new Paint();
        mPaint.setColor(mLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        switch (mDirection) {
            case SWING_RIGHT:
                canvas.drawLine(0, height, width, 0, mPaint);
                break;
            case SWING_LEFT:
                canvas.drawLine(0, 0, width, height, mPaint);
                break;
        }
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

    private void setAttributeSet(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.Slash);
        if (typedArray != null) {
            mLineColor = typedArray.getColor(R.styleable.Slash_color, DEFAULT_LINE_COLOR);
            mLineWidth = typedArray.getDimensionPixelOffset(R.styleable.Slash_width, DEFAULT_LINE_WIDTH);
            mDirection = typedArray.getInt(R.styleable.Slash_direction, DEFAULT_DIRECTION);
            typedArray.recycle();
        }
    }
}
