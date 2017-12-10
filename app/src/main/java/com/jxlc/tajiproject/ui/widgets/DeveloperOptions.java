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
import com.randal.aviana.LogUtils;
import com.unity3d.player.UnityPlayer;

import cn.pedant.SweetAlert.SweetAlertDialog;

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
    private SwitchButton mShowUnityBtnSwitch;

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

                float[] cod = coordinateTransformation(x, y);
                LogUtils.d(cod[0] + " " + cod[1]);
                UnityPlayer.UnitySendMessage("CanvasCtrol", "OnAndroidAddTowerCrane", tid + "#" + cod[0] + "#" + cod[1]);
            }
        });

        mDelBtn = (Button) findViewById(R.id.developer_del_button);
        mDelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = AntiCollisionAlgorithm.getInstance().getCheckTowerId();
                AntiCollisionAlgorithm.getInstance().removeTowerCraneById(id);
                UnityPlayer.UnitySendMessage("CanvasCtrol", "OnAndroidRemoveTowerCrane", "" + id);
            }
        });

        mWirelessSwitch = (SwitchButton)findViewById(R.id.developer_wireless_transmission_switch_button);
        mWirelessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d("access");
                if (!Transmitter.getInstance(mContext).isUsbReady()) {
                    new SweetAlertDialog(mContext)
                            .setTitleText("Remind")
                            .setContentText("无线状态不可用,请尝试重新连接无线设备!\n")
                            .show();
                    mWirelessSwitch.setChecked(false);
                } else {
                    if (b) {
                        Transmitter.getInstance(mContext).start();
                    } else {
                        Transmitter.getInstance(mContext).stop();
                    }
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
                    UnityPlayer.UnitySendMessage("CanvasCtrol", "onAndroidResumeAllTowerCrane", "");
                } else {
                    AntiCollisionAlgorithm.getInstance().stop();
                    UnityPlayer.UnitySendMessage("CanvasCtrol", "onAndroidStopAllTowerCrane", "");
                }
            }
        });
        mAllRunSwitch.setChecked(true);

        mShowUnityBtnSwitch = (SwitchButton)findViewById(R.id.developer_show_unity_btn_switch_button);
        mShowUnityBtnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    UnityPlayer.UnitySendMessage("CanvasCtrol", "OnAndroidSetButtonActive", "true");
                } else {
                    UnityPlayer.UnitySendMessage("CanvasCtrol", "OnAndroidSetButtonActive", "false");
                }
            }
        });
        mShowUnityBtnSwitch.setChecked(true);
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
}
