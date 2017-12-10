package com.jxlc.tajiproject.ui.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.algorithm.AntiCollisionAlgorithm;
import com.jxlc.tajiproject.algorithm.InfoListChangedListener;
import com.jxlc.tajiproject.bean.EnvironmentInfo;
import com.jxlc.tajiproject.bean.InfoListener;
import com.jxlc.tajiproject.bean.TowerCraneInfo;
import com.jxlc.tajiproject.listener.ScaleGesture;
import com.jxlc.tajiproject.listener.ScrollTapGesture;
import com.jxlc.tajiproject.ui.widgets.CircularProgressDrawable;
import com.jxlc.tajiproject.ui.widgets.TowerCrane2DView;
import com.randal.aviana.LogUtils;
import com.unity3d.player.UnityPlayer;

import static com.jxlc.tajiproject.bean.EnvironmentInfo.ENV_INFO_CHANGED_ID;

/**
 * Created by randal on 2017/5/4.
 */

public class ConstructionSiteLayout extends RelativeLayout implements InfoListChangedListener, InfoListener {
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

    private float scaleRateSite2Screen;
    private boolean firstLoadUnity = true;

    private static final int RESIZESITE_AND_TOWERCRANE = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESIZESITE_AND_TOWERCRANE:
                    this.post(new Runnable() {
                        @Override
                        public void run() {
                            resizeSiteAndTowerCrane();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

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
        initViews();
        AntiCollisionAlgorithm.getInstance().addListChangedListener(this);
        EnvironmentInfo.getInstance().addListener(this);
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

    // 0,0 -> -500, -500
    // 1000,1000 -> 500, 500
    // Unity's origin is in center
    private float[] coordinateTransformation(float x, float y) {
        float[] retCod = new float[2];
        retCod[0] = x - 500;
        retCod[1] = y - 500;
        return retCod;
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
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mContainer.getWidth(), mContainer.getHeight());
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

        float wRate = (float)cWidth / EnvironmentInfo.getInstance().getConstructionSiteWidth();
        float hRate = (float)cHeight / EnvironmentInfo.getInstance().getConstructionSiteHeight();
        LogUtils.d("" + wRate + " " + hRate);
        scaleRateSite2Screen = (wRate < hRate) ? wRate : hRate;

        mSiteBorder.removeAllViews();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)(EnvironmentInfo.getInstance().getConstructionSiteWidth() * scaleRateSite2Screen),
                (int)(EnvironmentInfo.getInstance().getConstructionSiteHeight() * scaleRateSite2Screen));
        lp.gravity = Gravity.CENTER;
        mSiteBorder.setLayoutParams(lp);

        for (TowerCraneInfo info : AntiCollisionAlgorithm.getInstance().getTCInfoList()) {
            TowerCrane2DView tcView = new TowerCrane2DView(mContext);
            info.addListener(this);
            tcView.setTowerCraneInfo(info);
            tcView.setScaleX(scaleRateSite2Screen);
            tcView.setScaleY(scaleRateSite2Screen);

            FrameLayout.LayoutParams tclp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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

    @Override
    public void onTowerCraneListChanged() {
        Message msg = new Message();
        msg.what = RESIZESITE_AND_TOWERCRANE;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onInfoChanged(int id) {
        if (id == ENV_INFO_CHANGED_ID) {              // only check env changed
            Message msg = new Message();
            msg.what = RESIZESITE_AND_TOWERCRANE;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onPaintInfoChanged(int id) {}

    @Override
    public void onStableInfoChanged(int id) {}

    @Override
    public void onLayoutInfoChanged(int id) {
        Message msg = new Message();
        msg.what = RESIZESITE_AND_TOWERCRANE;
        mHandler.sendMessage(msg);
    }
}





















