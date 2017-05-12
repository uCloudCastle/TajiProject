package com.jxlc.tajiproject.ui.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.bean.EnvironmentInfo;
import com.jxlc.tajiproject.bean.TowerCraneInfo;
import com.jxlc.tajiproject.listener.ScrollTapGesture;
import com.jxlc.tajiproject.listener.ScaleGesture;
import com.jxlc.tajiproject.ui.widgets.CircularProgressDrawable;
import com.jxlc.tajiproject.ui.widgets.TowerCrane2DView;
import com.randal.aviana.LogUtils;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by randal on 2017/5/4.
 */

public class ConstructionSiteLayout extends FrameLayout {
    private Context mContext;
    protected UnityPlayer mUnityPlayer;

    private ImageView mFloatBtn;
    private TextView mFloatBtnTextView;
    private CircularProgressDrawable mFloatBtnDrawable;
    private Animator mFloatBtnCurAnim;

    private FrameLayout mContainer;
    private FrameLayout mSiteBorder;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private List<TowerCraneInfo> mTCInfoList;
    private float scaleRateSite2Screen;
    private boolean firstLoadUnity = true;

    public ConstructionSiteLayout(@NonNull Context context) {
        this(context, null);
    }

    public ConstructionSiteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_constructionsite, this, true);
    }

    public ConstructionSiteLayout(Context context, AttributeSet attrs, UnityPlayer player) {
        this(context, attrs);
        mUnityPlayer = player;
        initVariable();
        initViews();
    }

    private void initVariable() {
        mTCInfoList = new ArrayList<>();
        TowerCraneInfo info1 = TowerCraneInfo.getDemoInfo();
        info1.setAngle(120);
        mTCInfoList.add(info1);
        TowerCraneInfo info2 = TowerCraneInfo.getDemoInfo();
        info2.setCoordinateX(info2.getCoordinateX() + info2.getFrontArmLength() + 10);
        info2.setCoordinateY(info2.getCoordinateY() + info2.getFrontArmLength() + 10);
        info2.setAngle(240);
        mTCInfoList.add(info2);
    }

    private void initViews() {
        mFloatBtn = (ImageView)findViewById(R.id.constructionsite_floatbtn);
        mFloatBtnDrawable = new CircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.layout_float_button_ring_width))
                .setOutlineColor(ContextCompat.getColor(mContext, android.R.color.transparent))
                .setRingColor(ContextCompat.getColor(mContext, android.R.color.holo_green_light))
                .setCenterColor(Color.parseColor("#E9DEEDFC"))
                .create();
        mFloatBtn.setImageDrawable(mFloatBtnDrawable);
        mFloatBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mFloatBtnDrawable.setCircleScale(0.75f);
                        break;
                    case MotionEvent.ACTION_UP:
                        switchView();
                        mFloatBtnDrawable.setCircleScale(0.95f);
                        if (firstLoadUnity) {
                            mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_loading));
                            mFloatBtnCurAnim = getLoadAnimation();
                            mFloatBtnCurAnim.start();
                            firstLoadUnity = false;
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mFloatBtnTextView = (TextView)findViewById(R.id.constructionsite_floatbtntext);
        mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_3d));
        mContainer = (FrameLayout)findViewById(R.id.constructionsite_container);
        mSiteBorder = (FrameLayout)findViewById(R.id.constructionsite_border);
        mContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                mScaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        mGestureDetector = new GestureDetector(mContext, new ScrollTapGesture(mSiteBorder));
        mScaleGestureDetector = new ScaleGestureDetector(mContext, new ScaleGesture(mSiteBorder));
        this.post(new Runnable() {
            @Override
            public void run() {
                resizeSiteAndTowerCrane();
            }
        });
    }

    private Animator getLoadAnimation() {
        AnimatorSet animation = new AnimatorSet();

        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(mFloatBtnDrawable, CircularProgressDrawable.PROGRESS_PROPERTY,
                0f, 1f);
        progressAnimation.setDuration(3600);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator colorAnimator = ObjectAnimator.ofInt(mFloatBtnDrawable, CircularProgressDrawable.RING_COLOR_PROPERTY,
                ContextCompat.getColor(mContext, android.R.color.holo_orange_dark),
                ContextCompat.getColor(mContext, android.R.color.holo_green_light));
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setDuration(3600);

        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFloatBtnDrawable.setProgress(0.0f);
                mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_2d));
            }
        });

        animation.playTogether(progressAnimation, colorAnimator);
        return animation;
    }

    private void switchView() {
        if (mContainer.getChildCount() == 3) {
            FrameLayout.LayoutParams lp = new LayoutParams(mContainer.getWidth(), mContainer.getHeight());
            mContainer.addView(mUnityPlayer, lp);
            mFloatBtn.bringToFront();
            mFloatBtnTextView.bringToFront();
            if (!firstLoadUnity) {
                mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_2d));
            }
        } else {
            mContainer.removeView(mUnityPlayer);
            mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_3d));
        }
    }

    private void resizeSiteAndTowerCrane() {
        int cWidth = mContainer.getWidth();
        int cHeight = mContainer.getHeight();
        LogUtils.d("" + cWidth + " " + cHeight);

        float wRate = (float)cWidth / (float)EnvironmentInfo.getInstance().getConstructionSiteWidth();
        float hRate = (float)cHeight / (float)EnvironmentInfo.getInstance().getConstructionSiteHeight();
        LogUtils.d("" + wRate + " " + hRate);
        scaleRateSite2Screen = (wRate < hRate) ? wRate : hRate;

        FrameLayout.LayoutParams lp = new LayoutParams((int)(EnvironmentInfo.getInstance().getConstructionSiteWidth() * scaleRateSite2Screen),
                (int)(EnvironmentInfo.getInstance().getConstructionSiteHeight() * scaleRateSite2Screen));
        lp.gravity = Gravity.CENTER;
        mSiteBorder.setLayoutParams(lp);

        for (TowerCraneInfo info : mTCInfoList) {
            TowerCrane2DView tcView = new TowerCrane2DView(mContext);
            tcView.setTowerCraneInfo(info);
            tcView.setScaleX(scaleRateSite2Screen);
            tcView.setScaleY(scaleRateSite2Screen);

            FrameLayout.LayoutParams tclp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//            tclp.setMargins((int)((info.getCoordinateX() - info.getFrontArmLength()) * scaleRate_Site2Screen) - (int)(info.getFrontArmLength() * (1-scaleRate_Site2Screen)),
//                    (int)((EnvironmentInfo.getInstance().getConstructionSiteHeight() - info.getCoordinateY() - info.getFrontArmLength()) * scaleRate_Site2Screen)  - (int)(info.getFrontArmLength() * (1-scaleRate_Site2Screen)),
//                    0, 0);
            tclp.setMargins((int)(info.getCoordinateX() * scaleRateSite2Screen - info.getFrontArmLength()),
                    (int)((EnvironmentInfo.getInstance().getConstructionSiteHeight() - info.getCoordinateY()) * scaleRateSite2Screen - info.getFrontArmLength()),
                    0, 0);
            mSiteBorder.addView(tcView, tclp);
        }
    }

    class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            LogUtils.d("access" + detector.getScaleFactor());
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            LogUtils.d("access" + detector.getScaleFactor());
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            LogUtils.d("access" + detector.getScaleFactor());
        }
    }
}





















