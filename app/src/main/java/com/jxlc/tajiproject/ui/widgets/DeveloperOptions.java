package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.algorithm.AntiCollisionAlgorithm;
import com.kyleduo.switchbutton.SwitchButton;
import com.randal.aviana.LogUtils;

/**
 * Created by Randal on 2017-05-16.
 */

public class DeveloperOptions extends LinearLayout {
    private Context mContext;
    private SwitchButton mRunSwitch;

    public DeveloperOptions(@NonNull Context context) {
        this(context, null);
    }

    public DeveloperOptions(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widgets_developeroptions, this, true);
        mContext = context;

        mRunSwitch = (SwitchButton)findViewById(R.id.developer_run_switch_button);
        mRunSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(" b = " + b);
                if (b) {
                    AntiCollisionAlgorithm.getInstance().run();
                } else {
                    AntiCollisionAlgorithm.getInstance().stop();
                }
            }
        });
        mRunSwitch.setChecked(true);
    }
}
