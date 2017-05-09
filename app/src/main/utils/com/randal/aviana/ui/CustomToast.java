package com.randal.aviana.ui;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;


public class CustomToast {
    public static final int LENGTH_SHORT = 2000;
    public static final int LENGTH_LONG = 3500;
    public static final int NO_IMG = -1;

    private static Vector<LinearLayout> mShowingLayouts = new Vector<>();
    private static WindowManager.LayoutParams params;
    private static WindowManager wm;

    /**
     * dismiss All CustomToast
     *
     * Notice: If you call makeText() and use Activity as Context,
     * then You should call this function in onDestory().
     *
     * Also, Another way is use BaseApplication.getContext()
     */
    synchronized public static void dismissAll() {
        for (LinearLayout layout : mShowingLayouts) {
            if (layout != null) {
                mShowingLayouts.remove(layout);
                wm.removeViewImmediate(layout);
            }
        }
    }

    /**
     * Show prompt information on screen
     *
     * If No background Image need to be display,
     * Set imgID = NO_IMG
     * Or It should be a res id
     */
    public static void makeText(Context context, int imgId, CharSequence text, String color, int duration) {
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final LinearLayout layout = new LinearLayout(context);
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextColor(Color.parseColor(color));
        tv.setTextSize(0, 30);
        tv.setGravity(Gravity.CENTER);

        if (imgId != NO_IMG) {
            layout.setBackgroundResource(imgId);
        }
        layout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addView(tv, lParams);

        params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.CENTER;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = android.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");

        wm.addView(layout, params);
        mShowingLayouts.add(layout);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (layout != null) {
                    dismissAll();
                }
            }
        }, duration);
    }
}
