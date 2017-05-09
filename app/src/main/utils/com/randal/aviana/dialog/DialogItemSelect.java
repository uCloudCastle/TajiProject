package com.randal.aviana.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.randal.aviana.LogUtils;
import com.randal.aviana.widgets.Arrow;
import com.randal.aviana.widgets.ScrollViewListened;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

/*
Usage:
    mDialog = new DialogItemSelect(this);
    mDialog.setTitle("Title");
    mDialog.setSubTitle("SubTitle");
    mDialog.setItems(new String[]{"11", "22", "33", "44", "55", "66", "77", "88", "99"});
    mDialog.setItemClickListener(new DialogItemSelect.OnItemClickListener() {
        @Override
        public void onItemSelected(int position) {
            LogUtils.d("" + position);
        }
    });
    mDialog.setItemBuilder(new DialogItemSelect.ItemBuilder<Button>() {
        @Override
        public Button build() {
            Button btn = new Button(MainActivity.this);
            btn.setGravity(Gravity.CENTER);
            btn.setTextSize(15);
            return btn;
        }
    });
    mDialog.setArrowEnable(true);
    mDialog.setItemNumPerPage(4);
    mDialog.setFocus(2);
    mDialog.show();
*/

public class DialogItemSelect<T extends View> extends Dialog implements View.OnClickListener {

    private Context mContext;
    private Drawable mBackground = null;
    private String[] mItems;
    private Vector<View> mItemViews;
    private OnItemClickListener mListener;
    private TextView mTitle;
    private TextView mSubTitle;
    private ItemBuilder<T> mBuilder = null;
    private Arrow mUpArrow = null;
    private Arrow mDownArrow = null;

    private int mPx_DialogMarginTop = 40;

    private boolean mEnable_Arrow = false;
    private int mPx_ArrowWidth = 60;
    private int mPx_ArrowHeight = 30;
    private int mArrowColor = Color.WHITE;
    private int mPx_ArrowSpacing = 20;

    private boolean mEnable_Title = false;
    private String mTitleStr = "";
    private float mPx_TitleSize = 63;
    private int mPx_TitleMarginBottom = 20;
    private int mTitleColor = Color.WHITE;

    private boolean mEnable_SubTitle = false;
    private String mSubTitleStr = "";
    private float mPx_SubTitleSize = 45;
    private int mPx_SubTitleMarginBottom = 40;
    private int mSubTitleColor = Color.WHITE;;

    private int mPx_ItemWidth = 450;
    private int mPx_ItemHeight = 90;
    private int mPx_ItemSpacing = 20;

    private int mItemNumPerPage = 3;
    private int mInitFocusPos = 0;

    public DialogItemSelect(Context context) {
        this(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    public DialogItemSelect(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mBackground != null) {
            getWindow().setBackgroundDrawable(mBackground);
        }

        LinearLayout mainLayout = new LinearLayout(mContext);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.addView(new View(mContext), new LinearLayout.LayoutParams(1, mPx_DialogMarginTop));

        if (mEnable_Title) {
            mTitle = new TextView(mContext);
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mPx_TitleSize);
            mTitle.setText(mTitleStr);
            mTitle.setTextColor(mTitleColor);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            titleParams.setMargins(0, 0, 0, mPx_TitleMarginBottom);
            mainLayout.addView(mTitle, titleParams);
        }

        if (mEnable_SubTitle) {
            mSubTitle = new TextView(mContext);
            mSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mPx_SubTitleSize);
            mSubTitle.setText(mSubTitleStr);
            mSubTitle.setTextColor(mSubTitleColor);
            LinearLayout.LayoutParams subTitleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            subTitleParams.setMargins(0, 0, 0, mPx_SubTitleMarginBottom);
            mainLayout.addView(mSubTitle, subTitleParams);
        }

        if (mEnable_Arrow) {
            mUpArrow = new Arrow(mContext, null, mArrowColor, 2f, Arrow.ARROW_UP);
            LinearLayout.LayoutParams upArrowParams = new LinearLayout.LayoutParams(mPx_ArrowWidth, mPx_ArrowHeight);
            upArrowParams.setMargins(0, 0, 0, mPx_ArrowSpacing);
            mainLayout.addView(mUpArrow, upArrowParams);
            mUpArrow.setVisibility(View.INVISIBLE);
        }

        ScrollViewListened itemView = new ScrollViewListened(mContext);
        itemView.setVerticalScrollBarEnabled(false);
        itemView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                mPx_ItemHeight * mItemNumPerPage + mPx_ItemSpacing * (mItemNumPerPage - 1)));
        itemView.setOnScrollListener(new ScrollViewListened.OnScrollListener() {
            @Override
            public void onBottomArrived() {
                if (mDownArrow != null) mDownArrow.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onBottomLeave() {
                if (mDownArrow != null) mDownArrow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTopArrived() {
                if (mUpArrow != null) mUpArrow.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTopLeave() {
                if (mUpArrow != null) mUpArrow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
            }
        });



        LinearLayout itemLayout = new LinearLayout(mContext);
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        if (mItems != null) {
            mItemViews = new Vector<>(mItems.length);
            if (mBuilder != null) {
                for (int i = 0; i < mItems.length; ++i) {
                    T item = mBuilder.build();
                    mItemViews.add(item);

                    LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                            mPx_ItemWidth, mPx_ItemHeight);
                    if (i != mItems.length - 1) {
                        itemParams.setMargins(0, 0, 0, mPx_ItemSpacing);
                    }
                    item.setLayoutParams(itemParams);
//                    item.setGravity(Gravity.CENTER);
//                    item.setText(mItems[i]);
                    item.setFocusable(true);
                    item.setTag(mItems[i]);
                    item.setOnClickListener(this);
                    itemLayout.addView(item);

                    try {
                        Method setText = item.getClass().getMethod("setText", String.class);
                        setText.invoke(item, mItems[i]);
                    } catch (Exception ex1) {
                        try {
                            Method setText = item.getClass().getMethod("setText", CharSequence.class);
                            setText.invoke(item, mItems[i]);
                        } catch (Exception ex2) {
                            LogUtils.w("no setText() for item!");
                        }
                    }
                }
            } else {
                for (int i = 0; i < mItems.length; ++i) {
                    Button item = new Button(mContext);
                    mItemViews.add(item);

                    LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(mPx_ItemWidth, mPx_ItemHeight);
                    if (i != mItems.length - 1) {
                        itemParams.setMargins(0, 0, 0, mPx_ItemSpacing);
                    }
                    item.setLayoutParams(itemParams);
                    item.setText(mItems[i]);
                    item.setFocusable(true);
                    item.setTag(mItems[i]);
                    item.setOnClickListener(this);
                    itemLayout.addView(item);
                }
            }

            if (mInitFocusPos > 0 && mInitFocusPos < mItemViews.size()) {
                mItemViews.elementAt(mInitFocusPos).requestFocus();
            }
        }

        itemView.addView(itemLayout);
        mainLayout.addView(itemView);

        if (mEnable_Arrow) {
            mDownArrow = new Arrow(mContext, null, mArrowColor, 2f, Arrow.ARROW_DOWN);
            LinearLayout.LayoutParams upArrowParams = new LinearLayout.LayoutParams(
                    mPx_ArrowWidth, mPx_ArrowHeight);
            upArrowParams.setMargins(0, mPx_ArrowSpacing, 0, 0);
            mainLayout.addView(mDownArrow, upArrowParams);
        }

        setContentView(mainLayout);
    }

    public void setItemBuilder(ItemBuilder<T> builder) {
        mBuilder = builder;
    }

    public void setBackgroundDrawable(Drawable drawable) {
        mBackground = drawable;
    }

    public void setTitleEnable(boolean enable) {
        mEnable_Title = enable;
    }

    public void setTitle(String title) {
        mEnable_Title = true;
        mTitleStr = title;
    }

    public void setDialogMarginTop(int pxVal) {
        mPx_DialogMarginTop = pxVal;
    }

    public void setTitleSize(float pxVal) {
        mPx_TitleSize = pxVal;
    }

    public void setTitleMarginBottom(int pxVal) {
        mPx_TitleMarginBottom = pxVal;
    }

    public void setTitleColor(int color) {
        mTitleColor = color;
    }

    public void setSubTitleEnable(boolean enable) {
        mEnable_SubTitle = enable;
    }

    public void setSubTitle(String title) {
        mEnable_SubTitle = true;
        mSubTitleStr = title;
    }

    public void setSubTitleSize(float pxVal) {
        mPx_SubTitleSize = pxVal;
    }

    public void setSubTitleMarginBottom(int pxVal) {
        mPx_SubTitleMarginBottom = pxVal;
    }

    public void setSubTitleColor(int color) {
        mSubTitleColor = color;
    }

    public void setArrowEnable(boolean enable) {
        mEnable_Arrow = enable;
    }

    public void setArrowSize(int pxw, int pxh) {
        mPx_ArrowWidth = pxw;
        mPx_ArrowHeight = pxh;
    }

    public void setArrowColor(int color) {
        mArrowColor = color;
    }

    public void setArrowSpacing(int pxVal) {
        mPx_ArrowSpacing = pxVal;
    }

    public void setItemSize(int pxw, int pxh) {
        mPx_ItemWidth = pxw;
        mPx_ItemHeight = pxh;
    }

    public int getItemWidth() {
        return mPx_ItemWidth;
    }

    public int getItemHeight() {
        return mPx_ItemHeight;
    }

    public void setItemSpacing(int pxVal) {
        mPx_ItemSpacing = pxVal;
    }

    public int getItemSpacing() {
        return mPx_ItemSpacing;
    }

    public void setItemNumPerPage(int num) {
        mItemNumPerPage = num;
    }

    public boolean setFocus(int pos) {
        if (mItemViews == null) {
            mInitFocusPos = pos;
            return true;
        }

        if (pos < 0 || pos >= mItemViews.size()) {
            return false;
        }
        return mItemViews.elementAt(pos).requestFocus();
    }

    public void setItems(@NonNull String[] items) {
        this.mItems = items;
        if (mItemViews != null) {
            mItemViews.clear();
            mInitFocusPos = 0;
        }
    }

    public void setItems(@NonNull List<String> items) {
        int size = items.size();
        String[] array = (String[])items.toArray(new String[size]);
        setItems(array);
    }

    public void setItemClickListener(OnItemClickListener mItemClickListener) {
        this.mListener = mItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }

        String tag = (String) v.getTag();
        for (int i = 0; i < mItems.length; i++) {
            if (TextUtils.equals(tag, mItems[i])) {
                mListener.onItemSelected(i);
                return;
            }
        }
    }

    public interface OnItemClickListener {
        void onItemSelected(int position);
    }

    public interface ItemBuilder<T extends View> {
        T build();
    }
}
