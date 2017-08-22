package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.algorithm.AntiCollisionAlgorithm;
import com.jxlc.tajiproject.bean.EnvironmentInfo;
import com.jxlc.tajiproject.bean.TowerCraneInfo;
import com.jxlc.tajiproject.transmitter.Transmitter;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Created by Randal on 2017-05-16.
 */

public class DeveloperOptions extends LinearLayout {
    private Context mContext;
    private Button mAddBtn;
    private Button mDelBtn;
    private SwitchButton mWirelessSwitch;
    private SwitchButton mSingleRunSwitch;
    private SwitchButton mAllRunSwitch;

    public DeveloperOptions(@NonNull Context context) {
        this(context, null);
    }

    public DeveloperOptions(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widgets_developeroptions, this, true);
        mContext = context;

        mAddBtn = (Button) findViewById(R.id.developer_add_button);
        mAddBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int tcSize = AntiCollisionAlgorithm.getInstance().getTCInfoList().size();
                if (tcSize >= 32) {
                    Toast.makeText(context, "已达到塔机上限", Toast.LENGTH_SHORT).show();
                    return;
                }

                TowerCraneInfo info = TowerCraneInfo.getDemoInfo();
                int tid;
                while (true) {
                    tid = (int)(Math.random() * 32) + 1;
                    if (AntiCollisionAlgorithm.getInstance().isIdExist(tid)) {
                        continue;
                    }
                    break;
                }

                float fl = (int)(Math.random() * 30) + 50;
                float ws = EnvironmentInfo.getInstance().getConstructionSiteWidth() - 2 * fl;
                float hs = EnvironmentInfo.getInstance().getConstructionSiteHeight() - 2 * fl;
                float x = (int)(Math.random() * ws);
                float y = (int)(Math.random() * hs);

                info.setIdentifier(tid);
                info.setFrontArmLength(fl);
                info.setCoordinateX(x);
                info.setCoordinateY(y);
                AntiCollisionAlgorithm.getInstance().addTowerCrane(info);
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

        mWirelessSwitch = (SwitchButton)findViewById(R.id.developer_wireless_transmission_switch_button);
        mWirelessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Transmitter.getInstance(mContext).start();
                } else {
                    Transmitter.getInstance(mContext).stop();
                }
            }
        });
        mWirelessSwitch.setChecked(false);

        mSingleRunSwitch = (SwitchButton)findViewById(R.id.developer_single_run_switch_button);
        mSingleRunSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    AntiCollisionAlgorithm.getInstance().stop();
                    AntiCollisionAlgorithm.getInstance().run(true);
                } else {
                    AntiCollisionAlgorithm.getInstance().stop();
                }
            }
        });
        mSingleRunSwitch.setChecked(false);

        mAllRunSwitch = (SwitchButton)findViewById(R.id.developer_all_run_switch_button);
        mAllRunSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    AntiCollisionAlgorithm.getInstance().stop();
                    AntiCollisionAlgorithm.getInstance().run(false);
                } else {
                    AntiCollisionAlgorithm.getInstance().stop();
                }
            }
        });
        mAllRunSwitch.setChecked(true);
    }
}
