package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.jxlc.tajiproject.algorithm.AntiCollisionAlgorithm;
import com.jxlc.tajiproject.algorithm.CheckChangedListener;
import com.jxlc.tajiproject.bean.TowerCraneInfo;

/**
 * Created by Randal on 2017-05-06.
 */

public class TowerCrane2DView extends View implements TowerCraneInfo.InfoListener, View.OnClickListener {
    private Context mContext;
    private TowerCraneInfo mTowerCraneInfo = TowerCraneInfo.getDemoInfo();
    private Paint mPaint;

    private boolean isChecked;

    private static final int TEXTSIZE = 15;
    private static final int TEXTSPACING = 10;

    private static final int INVALIDATE = 0;
    private static final int CHECKLOSE = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INVALIDATE:
                    invalidate();
                    break;
                case CHECKLOSE:
                    isChecked = false;
                    invalidate();
                    break;
                default:
                    break;
            }
        }
    };

    public TowerCrane2DView(Context context) {
        this(context, null);
    }

    public TowerCrane2DView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TowerCrane2DView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        setOnClickListener(this);
        AntiCollisionAlgorithm.getInstance().addCheckListener(new CheckChangedListener() {
            @Override
            public void onCheckChanged(int oldId, int newId) {
                if (oldId == mTowerCraneInfo.getIdentifier()) {
                    Message msg = new Message();
                    msg.what = CHECKLOSE;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    public void setTowerCraneInfo(TowerCraneInfo info) {
        mTowerCraneInfo = info;
        mTowerCraneInfo.addListener(this);
    }

    public TowerCraneInfo getTowerCraneInfo() {
        return mTowerCraneInfo;
    }

    @Override
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
            width = (int)(mTowerCraneInfo.getFrontArmLength() * 2);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int)(mTowerCraneInfo.getFrontArmLength() * 2);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int whalf = this.getWidth() / 2;
        int hhalf = this.getHeight() / 2;
        int radius = whalf < hhalf ? whalf : hhalf;
        double ap = mTowerCraneInfo.getAngle() * Math.PI / 180;
        float a = (float)Math.sin(ap);
        float b = (float)Math.cos(ap);

        mPaint.setStyle(Paint.Style.FILL);                        // 前臂圆
        if (isChecked) {
            mPaint.setColor(Color.parseColor("#B900FFFF"));
        } else {
            mPaint.setColor(Color.parseColor("#B900E100"));
        }
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, radius, mPaint);

        if (isChecked) {
            mPaint.setColor(Color.parseColor("#B900DDDD"));       // 后臂圆
        } else {
            mPaint.setColor(Color.parseColor("#B900B400"));
        }
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, mTowerCraneInfo.getRearArmLength(), mPaint);

        mPaint.setColor(Color.BLACK);                                          // 吊车
        canvas.drawCircle(whalf + mTowerCraneInfo.getTrolleyDistance() * b,
                hhalf - mTowerCraneInfo.getTrolleyDistance() * a, 3, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, mTowerCraneInfo.getRearArmLength(), mPaint);
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, mTowerCraneInfo.getFrontArmLength() - 1, mPaint);
        
        canvas.drawLine(whalf - mTowerCraneInfo.getRearArmLength() * b,         // 吊臂
                hhalf + mTowerCraneInfo.getRearArmLength() * a,
                whalf + mTowerCraneInfo.getFrontArmLength() * b,
                hhalf - mTowerCraneInfo.getFrontArmLength() * a, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(TEXTSIZE);                                            // id
        mPaint.setColor(Color.WHITE);
        canvas.drawText("#" + mTowerCraneInfo.getIdentifier(), whalf - mTowerCraneInfo.getRearArmLength() - TEXTSPACING,
                hhalf - mTowerCraneInfo.getRearArmLength() - TEXTSPACING, mPaint);
    }

    @Override
    public void onInfoChanged() {
    }

    @Override
    public void onPaintInfoChanged() {
        Message msg = new Message();
        msg.what = INVALIDATE;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onStableInfoChanged() {
    }

    @Override
    public void onClick(View view) {
        if (isChecked) {
            return;
        }
        AntiCollisionAlgorithm.getInstance().setCheckTowerId(mTowerCraneInfo.getIdentifier());
        this.isChecked = true;
        invalidate();
    }
}
