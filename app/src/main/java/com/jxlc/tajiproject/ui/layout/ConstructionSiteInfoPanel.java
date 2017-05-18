package com.jxlc.tajiproject.ui.layout;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.algorithm.AntiCollisionAlgorithm;
import com.jxlc.tajiproject.algorithm.AntiCollisionListener;
import com.jxlc.tajiproject.algorithm.IntersectValue;
import com.jxlc.tajiproject.ui.widgets.InfoPanelItemTitle;
import com.randal.aviana.ui.ExpandableLayout;

/**
 * Created by Randal on 2017-05-13.
 */

public class ConstructionSiteInfoPanel extends LinearLayout implements AntiCollisionListener {
    private Context mContext;
    private InfoPanelItemTitle mRunningStatusTitle;
    private ExpandableLayout expand_running_status;
    private NestedScrollView mStatusScrollView;
    private TextView mRunningStatusTextView;

    private InfoPanelItemTitle mTowerDataTitle;
    private ExpandableLayout expand_tower_data;

    private InfoPanelItemTitle mEnvDataTitle;
    private ExpandableLayout expand_env_data;

    private InfoPanelItemTitle mDeveloperTitle;
    private ExpandableLayout expand_developer;

    public static final int TEXTVIEW_MAXLINE = 30;

    public static final String BUNDLE_ID_1 = "id1";
    public static final String BUNDLE_ID_2 = "id2";
    public static final String BUNDLE_IS_OUTCIRCLE = "isOutCircle";
    public static final int HAS_INTERSECTION = 0;
    public static final int FRONT_ENTER = 1;
    public static final int REAR_ENTER = 2;
    public static final int FRONT_LEAVE = 3;
    public static final int REAR_LEAVE = 4;
    public static final int COLLISIONCOMING = 5;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HAS_INTERSECTION: {
                    Bundle bundle = msg.getData();
                    int id1 = bundle.getInt(BUNDLE_ID_1);
                    int id2 = bundle.getInt(BUNDLE_ID_2);
                    refreshStatus("塔机#" + id1 + "与塔机#" + id2 + "有交叉工作区域,请谨慎驾驶");
                    break;
                }
                case FRONT_ENTER: {
                    Bundle bundle = msg.getData();
                    int majorId = bundle.getInt(BUNDLE_ID_1);
                    int affectedId = bundle.getInt(BUNDLE_ID_2);
                    boolean isOutCircle = bundle.getBoolean(BUNDLE_IS_OUTCIRCLE);
                    refreshStatus("塔机#" + majorId + "前臂进入塔机#" + affectedId +
                            (isOutCircle ? "前臂" : "后臂") + "工作区域");
                    break;
                }
                case REAR_ENTER: {
                    Bundle bundle = msg.getData();
                    int majorId = bundle.getInt(BUNDLE_ID_1);
                    int affectedId = bundle.getInt(BUNDLE_ID_2);
                    refreshStatus("塔机#" + majorId + "后臂进入塔机#" + affectedId + "前臂工作区域");
                    break;
                }
                case FRONT_LEAVE: {
                    Bundle bundle = msg.getData();
                    int majorId = bundle.getInt(BUNDLE_ID_1);
                    int affectedId = bundle.getInt(BUNDLE_ID_2);
                    boolean isOutCircle = bundle.getBoolean(BUNDLE_IS_OUTCIRCLE);
                    refreshStatus("塔机#" + majorId + "前臂离开塔机#" + affectedId +
                            (isOutCircle ? "前臂" : "后臂") + "工作区域");
                    break;
                }
                case REAR_LEAVE: {
                    Bundle bundle = msg.getData();
                    int majorId = bundle.getInt(BUNDLE_ID_1);
                    int affectedId = bundle.getInt(BUNDLE_ID_2);
                    refreshStatus("塔机#" + majorId + "后臂离开塔机#" + affectedId + "前臂工作区域");
                    break;
                }
                case COLLISIONCOMING: {
                    Bundle bundle = msg.getData();
                    int id1 = bundle.getInt(BUNDLE_ID_1);
                    int id2 = bundle.getInt(BUNDLE_ID_2);
                    String text = "<font color='#FF6666'>塔机#" + id1 + "与塔机#" + id2 + "相距过近,请注意避让!</font>";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        refreshStatus(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        refreshStatus(Html.fromHtml(text));
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    public ConstructionSiteInfoPanel(@NonNull Context context) {
        this(context, null);
    }

    public ConstructionSiteInfoPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_siteinfopanel, this, true);
        mContext = context;

        mStatusScrollView = (NestedScrollView) findViewById(R.id.infopanel_runningstatus_scrollview);
        mRunningStatusTextView = (TextView) findViewById(R.id.infopanel_runningstatus_textview);
        expand_running_status = (ExpandableLayout) findViewById(R.id.expandable_running_status);
        mRunningStatusTitle = (InfoPanelItemTitle) findViewById(R.id.running_status_title);
        mRunningStatusTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expand_running_status.isExpanded()) {
                    mRunningStatusTitle.setDirection(InfoPanelItemTitle.ARROW_UP);
                    expand_running_status.collapse();
                } else {
                    mRunningStatusTitle.setDirection(InfoPanelItemTitle.ARROW_DOWN);
                    expand_running_status.expand();
                }
            }
        });

        expand_tower_data = (ExpandableLayout) findViewById(R.id.expandable_tower_data);
        mTowerDataTitle = (InfoPanelItemTitle) findViewById(R.id.tower_data_title);
        mTowerDataTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expand_tower_data.isExpanded()) {
                    mTowerDataTitle.setDirection(InfoPanelItemTitle.ARROW_UP);
                    expand_tower_data.collapse();
                } else {
                    mTowerDataTitle.setDirection(InfoPanelItemTitle.ARROW_DOWN);
                    expand_tower_data.expand();
                }
            }
        });

        expand_env_data = (ExpandableLayout) findViewById(R.id.expandable_env_data);
        mEnvDataTitle = (InfoPanelItemTitle) findViewById(R.id.env_data_title);
        mEnvDataTitle.setDirection(InfoPanelItemTitle.ARROW_UP);
        mEnvDataTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expand_env_data.isExpanded()) {
                    mEnvDataTitle.setDirection(InfoPanelItemTitle.ARROW_UP);
                    expand_env_data.collapse();
                } else {
                    mEnvDataTitle.setDirection(InfoPanelItemTitle.ARROW_DOWN);
                    expand_env_data.expand();
                }
            }
        });

        expand_developer = (ExpandableLayout) findViewById(R.id.expandable_developer);
        mDeveloperTitle = (InfoPanelItemTitle) findViewById(R.id.developer_title);
        mDeveloperTitle.setDirection(InfoPanelItemTitle.ARROW_UP);
        mDeveloperTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expand_developer.isExpanded()) {
                    mDeveloperTitle.setDirection(InfoPanelItemTitle.ARROW_UP);
                    expand_developer.collapse();
                } else {
                    mDeveloperTitle.setDirection(InfoPanelItemTitle.ARROW_DOWN);
                    expand_developer.expand();
                }
            }
        });

        AntiCollisionAlgorithm.getInstance().addListener(this);
    }

    public void refreshStatus(String msg) {
        mRunningStatusTextView.append("\n" + msg);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mStatusScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void refreshStatus(Spanned msg) {
        mRunningStatusTextView.append("\n");
        mRunningStatusTextView.append(msg);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mStatusScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onHasIntersection(int id1, int id2, IntersectValue value) {
        Message msg = new Message();
        msg.what = HAS_INTERSECTION;
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID_1, id1);
        bundle.putInt(BUNDLE_ID_2, id2);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onFrontEnterIntersection(int majorId, int affectedId, boolean isOutCircle) {
        Message msg = new Message();
        msg.what = FRONT_ENTER;
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID_1, majorId);
        bundle.putInt(BUNDLE_ID_2, affectedId);
        bundle.putBoolean(BUNDLE_IS_OUTCIRCLE, isOutCircle);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onRearEnterIntersection(int majorId, int affectedId) {
        Message msg = new Message();
        msg.what = REAR_ENTER;
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID_1, majorId);
        bundle.putInt(BUNDLE_ID_2, affectedId);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onFrontLeaveIntersection(int majorId, int affectedId, boolean isOutCircle) {
        Message msg = new Message();
        msg.what = FRONT_LEAVE;
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID_1, majorId);
        bundle.putInt(BUNDLE_ID_2, affectedId);
        bundle.putBoolean(BUNDLE_IS_OUTCIRCLE, isOutCircle);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onRearLeaveIntersection(int majorId, int affectedId) {
        Message msg = new Message();
        msg.what = REAR_LEAVE;
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID_1, majorId);
        bundle.putInt(BUNDLE_ID_2, affectedId);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onCollisionComing(int id1, int id2) {
        Message msg = new Message();
        msg.what = COLLISIONCOMING;
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID_1, id1);
        bundle.putInt(BUNDLE_ID_2, id2);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
}
