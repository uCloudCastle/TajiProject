package com.jxlc.tajiproject.ui.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.ui.widgets.InfoPanelItemTitle;
import com.randal.aviana.ui.ExpandableLayout;

/**
 * Created by Randal on 2017-05-13.
 */

public class ConstructionSiteInfoPanel extends LinearLayout {
    private Context mContext;
    private InfoPanelItemTitle mRunningStatusTitle;
    private ExpandableLayout expand_running_status;
    private TextView mRunningStatusTextView;

    private InfoPanelItemTitle mTowerDataTitle;
    private ExpandableLayout expand_tower_data;

    private InfoPanelItemTitle mEnvDataTitle;
    private ExpandableLayout expand_env_data;

    private InfoPanelItemTitle mDeveloperTitle;
    private ExpandableLayout expand_developer;

    public ConstructionSiteInfoPanel(@NonNull Context context) {
        this(context, null);
    }

    public ConstructionSiteInfoPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_siteinfopanel, this, true);
        mContext = context;

        mRunningStatusTextView = (TextView) findViewById(R.id.infopanel_runningstatus_textview);
        expand_running_status = (ExpandableLayout) findViewById(R.id.expandable_running_status);
        mRunningStatusTitle = (InfoPanelItemTitle)findViewById(R.id.running_status_title);
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
    }
}
