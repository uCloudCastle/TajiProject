package com.jxlc.tajiproject.ui.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jxlc.tajiproject.R;
import com.randal.aviana.LogUtils;

/**
 * Created by Randal on 2017-05-13.
 */

public class ConstructionSiteInfoPanel extends LinearLayout {
    private Context mContext;
    private ConstructionSiteLayout mSiteLayout;
    private Button mBtn;

    public ConstructionSiteInfoPanel(@NonNull Context context) {
        this(context, null);
    }

    public ConstructionSiteInfoPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_siteinfopanel, this, true);
        mContext = context;

        mBtn = (Button)findViewById(R.id.siteinfopanel_btn);
        mBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.d("access");

                mSiteLayout.mUnityPlayer.UnitySendMessage("taji", "StartAnimator", "");
            }
        });
    }

    public void setSiteLayout(ConstructionSiteLayout layout) {
        mSiteLayout = layout;
    }
}
