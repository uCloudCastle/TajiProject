package com.jxlc.tajiproject.ui.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jxlc.tajiproject.R;
import com.randal.aviana.LogUtils;
import com.randal.aviana.data.DataHolderBuilder;
import com.randal.aviana.data.DataHolderBuilder.DataHolder;

import java.util.List;

/**
 * Created by randal on 2017/5/4.
 */

public class ConfigureLayout extends FrameLayout {
    private Context mContext;
    private TextView mText1;
    private TextView mText2;
    private TextView mBtn;

    public ConfigureLayout(@NonNull Context context) {
        this(context, null);
    }

    public ConfigureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_configure, this, true);
        mContext = context;

        mText1 = (TextView)findViewById(R.id.con_textview1);
        mText2 = (TextView)findViewById(R.id.con_textview2);
        mBtn = (Button)findViewById(R.id.con_btn);

        MyData ee = new MyData();
        final int eeid = ee.hashCode();
        final DataHolder<MyData> sing = DataHolderBuilder.getSingleton(MyData.class);
        sing.addObject(ee);
        sing.registerObjectChangedListener(new DataHolderBuilder.OnObjectChangedListener() {
            @Override
            public void OnObjectChanged(int hashCode, int part) {
                LogUtils.d(hashCode + " ffff " + part + " " + eeid);
                if (hashCode == eeid) {
                    mText1.setText(sing.getObjectByHashCode(hashCode).toString());
                }
            }
        });

        sing.registerObjectListChangedListener(new DataHolderBuilder.OnObjectListChangedListener() {
            @Override
            public void OnObjectAdded(int hashCode) {
                LogUtils.d(hashCode + " ");
                mText2.setText(sing.getObjectList().toString());
            }

            @Override
            public void OnObjectRemoved(int hashCode) {
                LogUtils.d(hashCode + " ");
                mText2.setText(sing.getObjectList().toString());
            }

            @Override
            public void OnObjectListChanged() {
                LogUtils.d(" ");
                mText2.setText(sing.getObjectList().toString());
            }
        });

        mBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                List<MyData> data = sing.getObjectList();
                data.get(0).setI(33);
                data.get(0).setF(10.12f);
                data.get(0).setS("876");
                DataHolderBuilder.getSingleton(MyData.class).addObject(new MyData());
            }
        });
    }

    class MyData {
        int i = 3;
        float f = 4.4f;
        String srt = "123";

        public void setI(int aa) {
            i = aa;
            DataHolderBuilder.getSingleton(MyData.class).notifyObjectChanged(this.hashCode(), 1);
        }

        public void setF(float aa) {
            f = aa;
            DataHolderBuilder.getSingleton(MyData.class).notifyObjectChanged(this.hashCode(), 2);
        }

        public void setS(String aa) {
            srt = aa;
            DataHolderBuilder.getSingleton(MyData.class).notifyObjectChanged(this.hashCode(), 3);
        }

        @Override
        public String toString() {
            return "MyData{" +
                    "i=" + i +
                    ", f=" + f +
                    ", srt=" + srt +
                    '}';
        }
    }
}
