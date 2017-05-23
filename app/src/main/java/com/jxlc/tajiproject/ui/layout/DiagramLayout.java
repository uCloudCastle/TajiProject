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
import com.randal.aviana.database.KeyValueTable;
import com.randal.aviana.database.SQLiteUtils;

/**
 * Created by randal on 2017/5/4.
 */

public class DiagramLayout extends FrameLayout {
    private Context mContext;
    private TextView mText;
    private Button mBtn;
    int pos = 0;

    public DiagramLayout(@NonNull Context context) {
        this(context, null);
    }

    public DiagramLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_diagram, this, true);
        mContext = context;

        final String key[] = {"11", "22", "33"};
        final String value[] = {"77", "88", "99"};

        mText = (TextView)findViewById(R.id.diagram_textview);
        mBtn = (Button)findViewById(R.id.diagram_btn);
        final KeyValueTable table = SQLiteUtils.createOrOpenKeyValueTable(context, "testdb", "testtable");

        mBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos >= 3) {
                    pos = 0;
                }

                table.insert(key[pos], value[pos]);
                ++pos;
            }
        });
    }
}
