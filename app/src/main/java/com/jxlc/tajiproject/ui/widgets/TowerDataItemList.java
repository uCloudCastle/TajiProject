package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.algorithm.AntiCollisionAlgorithm;
import com.jxlc.tajiproject.algorithm.CheckChangedListener;
import com.jxlc.tajiproject.bean.TowerCraneInfo;

/**
 * Created by randal on 2017/5/16.
 */

public class TowerDataItemList extends LinearLayout implements CheckChangedListener {

    private final static int INDICATOR_COLOR_NON = Color.parseColor("#808080");
    private final static int INDICATOR_COLOR_OK = Color.parseColor("#00CC00");
    private final static int INDICATOR_COLOR_ERROR = Color.parseColor("#CC0000");

    private Context mContext;
    private ListDataItem mItemId;
    private ListDataItem mItemModel;
    private ListDataItem mItemX;
    private ListDataItem mItemY;
    private ListDataItem mItemFrontArm;
    private ListDataItem mItemRearArm;
    private ListDataItem mItemHeight;
    private ListDataItem mItemTrolleyDistance;
    private ListDataItem mItemRopeLength;
    private ListDataItem mItemAngle;
    private StatusIndicatorItem mIndicatorLiftweight;
    private StatusIndicatorItem mIndicatorLiftheight;
    private StatusIndicatorItem mIndicatorTorque;
    private StatusIndicatorItem mIndicatorOverstorke;
    private StatusIndicatorItem mIndicatorSlewing;

    private static final int REINPUT = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REINPUT:
                    inputData();
                    break;
                default:
                    break;
            }
        }
    };

    public TowerDataItemList(@NonNull Context context) {
        this(context, null);
    }

    public TowerDataItemList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widgets_towerdataitemlist, this, true);

        mItemId = (ListDataItem) findViewById(R.id.tower_data_item_id);
        mItemId.addInputResultListener(new ListDataItem.OnInputResultListener() {
            @Override
            public void OnInputResult(String str) {
                int setId = 0;
                try {
                    setId = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (AntiCollisionAlgorithm.getInstance().isIdExist(setId)) {
                    Toast.makeText(mContext, "不支持重复的塔机ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo().setIdentifier(setId);
            }
        });

        mItemModel = (ListDataItem) findViewById(R.id.tower_data_item_model);
        mItemModel.addInputResultListener(new ListDataItem.OnInputResultListener() {
            @Override
            public void OnInputResult(String str) {
                AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo().setModelName(str);
            }
        });

        mItemX = (ListDataItem) findViewById(R.id.tower_data_item_x);
        mItemX.addInputResultListener(new ListDataItem.OnInputResultListener() {
            @Override
            public void OnInputResult(String str) {
                try {
                    float x = Float.parseFloat(str);
                    AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo().setCoordinateX(x);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        mItemY = (ListDataItem) findViewById(R.id.tower_data_item_y);
        mItemY.addInputResultListener(new ListDataItem.OnInputResultListener() {
            @Override
            public void OnInputResult(String str) {
                try {
                    float y = Float.parseFloat(str);
                    AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo().setCoordinateY(y);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        mItemFrontArm = (ListDataItem) findViewById(R.id.tower_data_item_frontarm);
        mItemFrontArm.addInputResultListener(new ListDataItem.OnInputResultListener() {
            @Override
            public void OnInputResult(String str) {
                try {
                    float front = Float.parseFloat(str);
                    AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo().setFrontArmLength(front);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        mItemRearArm = (ListDataItem) findViewById(R.id.tower_data_item_reararm);
        mItemRearArm.addInputResultListener(new ListDataItem.OnInputResultListener() {
            @Override
            public void OnInputResult(String str) {
                try {
                    float rear = Float.parseFloat(str);
                    AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo().setRearArmLength(rear);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        mItemHeight = (ListDataItem) findViewById(R.id.tower_data_item_height);
        mItemHeight.addInputResultListener(new ListDataItem.OnInputResultListener() {
            @Override
            public void OnInputResult(String str) {
                try {
                    float height = Float.parseFloat(str);
                    AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo().setArmToGroundHeight(height);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        mItemTrolleyDistance = (ListDataItem) findViewById(R.id.tower_data_item_trolleydistance);
        mItemTrolleyDistance.addInputResultListener(new ListDataItem.OnInputResultListener() {
            @Override
            public void OnInputResult(String str) {
                try {
                    float td = Float.parseFloat(str);
                    AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo().setTrolleyDistance(td);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        mItemRopeLength = (ListDataItem) findViewById(R.id.tower_data_item_ropelength);
        mItemRopeLength.addInputResultListener(new ListDataItem.OnInputResultListener() {
            @Override
            public void OnInputResult(String str) {
                try {
                    float ropelen = Float.parseFloat(str);
                    AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo().setRopeLength(ropelen);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        mItemAngle = (ListDataItem) findViewById(R.id.tower_data_item_angle);
        mItemAngle.addInputResultListener(new ListDataItem.OnInputResultListener() {
            @Override
            public void OnInputResult(String str) {
                try {
                    float angle = Float.parseFloat(str);
                    AntiCollisionAlgorithm.getInstance().getCurTowerCraneInfo().setAngle(angle);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        mIndicatorLiftweight = (StatusIndicatorItem) findViewById(R.id.tower_data_item_limiter_liftweight);
        mIndicatorLiftheight = (StatusIndicatorItem) findViewById(R.id.tower_data_item_limiter_liftheight);
        mIndicatorTorque = (StatusIndicatorItem) findViewById(R.id.tower_data_item_limiter_torque);
        mIndicatorOverstorke = (StatusIndicatorItem) findViewById(R.id.tower_data_item_limiter_overstorke);
        mIndicatorSlewing = (StatusIndicatorItem) findViewById(R.id.tower_data_item_limiter_slewing);
        inputData();

        AntiCollisionAlgorithm.getInstance().addCheckListener(this);
    }

    private void inputData() {
        int id = AntiCollisionAlgorithm.getInstance().getCheckTowerId();
        TowerCraneInfo info = AntiCollisionAlgorithm.getInstance().getTowerCraneInfoById(id);
        if (info == null) {
            clearEdit();
            return;
        }

        mItemId.setContent(String.valueOf(info.getIdentifier()));
        mItemModel.setContent(info.getModelName());
        mItemX.setContent(String.valueOf(info.getCoordinateX()));
        mItemY.setContent(String.valueOf(info.getCoordinateY()));
        mItemFrontArm.setContent(String.valueOf(info.getFrontArmLength()));
        mItemRearArm.setContent(String.valueOf(info.getRearArmLength()));
        mItemHeight.setContent(String.valueOf(info.getArmToGroundHeight()));
        mItemTrolleyDistance.setContent(String.valueOf(info.getTrolleyDistance()));
        mItemRopeLength.setContent(String.valueOf(info.getRopeLength()));
        mItemAngle.setContent(String.valueOf(info.getAngle()));
        mIndicatorLiftweight.setIndicatorColor(info.isLiftWeightLimiterWorkFine() ? INDICATOR_COLOR_OK : INDICATOR_COLOR_ERROR);
        mIndicatorLiftheight.setIndicatorColor(info.isLiftHeightLimiterWorkFine() ? INDICATOR_COLOR_OK : INDICATOR_COLOR_ERROR);
        mIndicatorTorque.setIndicatorColor(info.isTorqueLimiterWorkFine() ? INDICATOR_COLOR_OK : INDICATOR_COLOR_ERROR);
        mIndicatorOverstorke.setIndicatorColor(info.isOverstrokeLimiterWorkFine() ? INDICATOR_COLOR_OK : INDICATOR_COLOR_ERROR);
        mIndicatorSlewing.setIndicatorColor(info.isSlewingLimiterWorkFine() ? INDICATOR_COLOR_OK : INDICATOR_COLOR_ERROR);
    }

    private void clearEdit() {
        mItemId.setContent("");
        mItemModel.setContent("");
        mItemX.setContent("");
        mItemY.setContent("");
        mItemFrontArm.setContent("");
        mItemRearArm.setContent("");
        mItemHeight.setContent("");
        mItemTrolleyDistance.setContent("");
        mItemRopeLength.setContent("");
        mItemAngle.setContent("");
        mIndicatorLiftweight.setIndicatorColor(INDICATOR_COLOR_NON);
        mIndicatorLiftheight.setIndicatorColor(INDICATOR_COLOR_NON);
        mIndicatorTorque.setIndicatorColor(INDICATOR_COLOR_NON);
        mIndicatorOverstorke.setIndicatorColor(INDICATOR_COLOR_NON);
        mIndicatorSlewing.setIndicatorColor(INDICATOR_COLOR_NON);
    }

    @Override
    public void onCheckChanged(int oldId, int newId) {
        Message msg = new Message();
        msg.what = REINPUT;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onCheckedDataChanged() {
        Message msg = new Message();
        msg.what = REINPUT;
        mHandler.sendMessage(msg);
    }
}
