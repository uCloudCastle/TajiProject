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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.ui.widgets.CircularProgressDrawable;
import com.unity3d.player.UnityPlayer;

/**
 * Created by randal on 2017/5/4.
 */

public class TowerCraneLayout extends FrameLayout {
    private Context mContext;
    protected UnityPlayer mUnityPlayer;

    private ImageView mFloatBtn;
    private TextView mFloatBtnTextView;
    private CircularProgressDrawable mFloatBtnDrawable;
    private Animator mFloatBtnCurAnim;

    private boolean firstLoadUnity = true;

    public TowerCraneLayout(@NonNull Context context) {
        this(context, null);
    }

    public TowerCraneLayout(Context context, AttributeSet attrs, UnityPlayer player) {
        this(context, attrs);
        mUnityPlayer = player;
    }

    public TowerCraneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_towercrane, this, true);

        mFloatBtn = (ImageView)findViewById(R.id.towercrane_floatbtn);
        mFloatBtnDrawable = new CircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.layout_float_button_ring_width))
                .setOutlineColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setRingColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
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

        mFloatBtnTextView = (TextView)findViewById(R.id.towercrane_floatbtntext);
        mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_3d));
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
                mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_monitor));
            }
        });

        animation.playTogether(progressAnimation, colorAnimator);
        return animation;
    }

    private void switchView() {
        FrameLayout container = (FrameLayout)findViewById(R.id.towercrane_container);
        if (container.getChildCount() == 1) {
            FrameLayout.LayoutParams lp = new LayoutParams(container.getWidth(), container.getHeight());
            container.addView(mUnityPlayer, lp);
            if (!firstLoadUnity) {
                mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_monitor));
            }
        } else {
            container.removeView(mUnityPlayer);
            mFloatBtnTextView.setText(getResources().getText(R.string.floatbtn_3d));
        }
    }
}
