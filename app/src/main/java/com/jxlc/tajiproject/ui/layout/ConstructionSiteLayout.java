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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.bean.EnvironmentInfo;
import com.jxlc.tajiproject.bean.TowerCraneInfo;
import com.jxlc.tajiproject.ui.widgets.CircularProgressDrawable;
import com.jxlc.tajiproject.ui.widgets.TowerCrane2DView;
import com.randal.aviana.LogUtils;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.mode;

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

    private FrameLayout mSiteBorder;
    private List<TowerCraneInfo> mTCInfoList;
    private float scaleRate;

    private boolean firstLoadUnity = true;
    int touchNum = 0;
    float oldDist = 0.0f;

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
        mTCInfoList.add(TowerCraneInfo.getDemoInfo());
    }

    private void initViews() {
        mFloatBtn = (ImageView)findViewById(R.id.constructionsite_floatbtn);
        mFloatBtnDrawable = new CircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.layout_float_button_ring_width))
                .setOutlineColor(ContextCompat.getColor(mContext, android.R.color.transparent))
                .setRingColor(ContextCompat.getColor(mContext, android.R.color.holo_green_light))
                .setCenterColor(Color.parseColor("#EFFFFAFA"))
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

        mSiteBorder = (FrameLayout)findViewById(R.id.constructionsite_border);
        mSiteBorder.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        touchNum = 1;
                        break;
                    case MotionEvent.ACTION_UP:
                        touchNum = 0;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        touchNum = 1;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        touchNum += 1;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (mode >= 2) {
                            float newDist = spacing(event);
                            if (Math.abs(newDist - oldDist) > 30 && Math.abs(newDist - oldDist) < 40) {
//                                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)mSiteBorder.getLayoutParams();
//                                LogUtils.d(" " + newDist + " " + oldDist + " " + lp.width + " " + lp.height);
//                                lp.width = (int)(lp.width * scaleRate * (newDist / oldDist));
//                                lp.height = (int)(lp.height * scaleRate * (newDist / oldDist));
//                                mSiteBorder.setLayoutParams(lp);
//                                oldDist = newDist;
//                                LogUtils.d(" "+ touchNum + " " + oldDist + " " + lp.width + " " + lp.height);
                                mSiteBorder.setScaleX(scaleRate * newDist / oldDist);
                                mSiteBorder.setScaleY(scaleRate * newDist / oldDist);
                                oldDist = newDist;

                            }
                        }
                        break;
                }
                return true;
            }

            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.sqrt(x * x + y * y);
            }
        });
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
        FrameLayout container = (FrameLayout)findViewById(R.id.constructionsite_container);
        if (container.getChildCount() == 3) {
            FrameLayout.LayoutParams lp = new LayoutParams(container.getWidth(), container.getHeight());
            container.addView(mUnityPlayer, lp);
            mFloatBtn.bringToFront();
            mFloatBtnTextView.bringToFront();
            if (!firstLoadUnity) {
                mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_2d));
            }
        } else {
            container.removeView(mUnityPlayer);
            mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_3d));
        }
    }

    private void resizeSiteAndTowerCrane() {
        FrameLayout container = (FrameLayout)findViewById(R.id.constructionsite_container);
        int cWidth = container.getWidth();
        int cHeight = container.getHeight();
        LogUtils.d("" + cWidth + " " + cHeight);

        float wRate = (float)cWidth / (float)EnvironmentInfo.getInstance().getConstructionSiteWidth();
        float hRate = (float)cHeight / (float)EnvironmentInfo.getInstance().getConstructionSiteHeight();
        LogUtils.d("" + wRate + " " + hRate);
        scaleRate = (wRate < hRate) ? wRate : hRate;

        FrameLayout.LayoutParams lp = new LayoutParams((int)(EnvironmentInfo.getInstance().getConstructionSiteWidth() * scaleRate),
                (int)(EnvironmentInfo.getInstance().getConstructionSiteHeight() * scaleRate));
        lp.gravity = Gravity.CENTER;
        mSiteBorder.setLayoutParams(lp);

        for (TowerCraneInfo info : mTCInfoList) {
            TowerCrane2DView tcView = new TowerCrane2DView(mContext);
            tcView.setTowerCraneInfo(info);
            tcView.setScaleX(scaleRate);
            tcView.setScaleY(scaleRate);

            FrameLayout.LayoutParams tclp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//            tclp.setMargins((int)((info.getCoordinateX() - info.getFrontArmLength()) * scaleRate) - (int)(info.getFrontArmLength() * (1-scaleRate)),
//                    (int)((EnvironmentInfo.getInstance().getConstructionSiteHeight() - info.getCoordinateY() - info.getFrontArmLength()) * scaleRate)  - (int)(info.getFrontArmLength() * (1-scaleRate)),
//                    0, 0);
            tclp.setMargins((int)(info.getCoordinateX() * scaleRate - info.getFrontArmLength()),
                    (int)((EnvironmentInfo.getInstance().getConstructionSiteHeight() - info.getCoordinateY()) * scaleRate  - info.getFrontArmLength()),
                    0, 0);
            mSiteBorder.addView(tcView, tclp);
        }
    }


}





















