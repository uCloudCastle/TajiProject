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
import com.randal.aviana.database.SQLiteUtils;
import com.randal.aviana.database.TextTable;

/**
 * Created by randal on 2017/5/4.
 */

public class DiagramLayout extends FrameLayout {
    private Context mContext;
    private TextView mText;
    private TextView mText2;
    private TextView mText3;
    private Button mBtn;

    public DiagramLayout(@NonNull Context context) {
        this(context, null);
    }

    public DiagramLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_diagram, this, true);
        mContext = context;

        final String[] value = {"11", "22", "33", "44", "55"};
        final TextTable table = SQLiteUtils.createOrOpenTextTable(context, "test.db", "txttable",
                new String[]{"a", "b", "c", "d", "e"}, null);

        mText = (TextView)findViewById(R.id.diagram_textview);
        mText.setText(table.queryAll2String());

        mText2 = (TextView)findViewById(R.id.diagram_textview2);
        mText3 = (TextView)findViewById(R.id.diagram_textview3);
        mBtn = (Button)findViewById(R.id.diagram_btn);

        mBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                long id = table.insert(value);
                LogUtils.d("id = " + id);

                mText.setText(table.queryAll2String());
            }
        });

        ((Button)findViewById(R.id.diagram_btn2)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] value = table.query("1");
                mText2.setText(value.toString());
            }
        });

        ((Button)findViewById(R.id.diagram_btn3)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                table.delete("1");
                mText.setText(table.queryAll2String());
            }
        });

//        table.addKeyValueTableListener(new KeyValueTable.KeyValueTableListener() {
//            @Override
//            public void onValueAdded(String key, String value) {
//                mText3.setText(table.getPairListString());
//            }
//
//            @Override
//            public void onValueUpdated(String key, String value) {
//                mText3.setText(table.getPairListString());
//            }
//
//            @Override
//            public void onValueRemoved(String key) {
//                mText3.setText(table.getPairListString());
//            }
//        });
    }
}
