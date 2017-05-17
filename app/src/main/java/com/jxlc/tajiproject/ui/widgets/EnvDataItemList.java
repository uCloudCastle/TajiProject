package com.jxlc.tajiproject.ui.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.bean.EnvironmentInfo;
import com.jxlc.tajiproject.bean.InfoListener;

/**
 * Created by randal on 2017/5/16.
 */

public class EnvDataItemList extends LinearLayout implements InfoListener {
    private Context mContext;
    private ListDataItem mItemX;
    private ListDataItem mItemY;
    private ListDataItem mItemWindSpeed;
    private ListDataItem mItemTemperature;

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

    public EnvDataItemList(@NonNull Context context) {
        this(context, null);
    }

    public EnvDataItemList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widgets_envdataitemlist, this, true);

        mItemX = (ListDataItem) findViewById(R.id.env_data_item_x);
        mItemY = (ListDataItem) findViewById(R.id.env_data_item_y);
        mItemWindSpeed = (ListDataItem) findViewById(R.id.env_data_item_windspeed);
        mItemTemperature = (ListDataItem) findViewById(R.id.env_data_item_temperature);
        inputData();

        EnvironmentInfo.getInstance().addListener(this);
    }

    private void inputData() {
        mItemX.setContent(String.valueOf(EnvironmentInfo.getInstance().getConstructionSiteWidth()));
        mItemY.setContent(String.valueOf(EnvironmentInfo.getInstance().getConstructionSiteHeight()));
        mItemWindSpeed.setContent(String.valueOf(EnvironmentInfo.getInstance().getWindSpeed()));
        mItemTemperature.setContent(String.valueOf(EnvironmentInfo.getInstance().getTemperature()));
    }

    @Override
    public void onInfoChanged(int id) {
        Message msg = new Message();
        msg.what = REINPUT;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onStableInfoChanged(int id) {}

    @Override
    public void onPaintInfoChanged(int id) {}
}
