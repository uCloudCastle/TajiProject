package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.algorithm.AntiCollisionAlgorithm;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Created by Randal on 2017-05-16.
 */

public class DeveloperOptions extends LinearLayout {
    private Context mContext;
    private Button mAddBtn;
    private Button mDelBtn;
    private SwitchButton mRunSwitch;

    public DeveloperOptions(@NonNull Context context) {
        this(context, null);
    }

    public DeveloperOptions(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widgets_developeroptions, this, true);
        mContext = context;

        mAddBtn = (Button) findViewById(R.id.developer_add_button);
        mAddBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mDelBtn = (Button) findViewById(R.id.developer_del_button);
        mDelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = AntiCollisionAlgorithm.getInstance().getCheckTowerId();
                AntiCollisionAlgorithm.getInstance().removeTowerCraneById(id);
            }
        });

        mRunSwitch = (SwitchButton)findViewById(R.id.developer_run_switch_button);
        mRunSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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
