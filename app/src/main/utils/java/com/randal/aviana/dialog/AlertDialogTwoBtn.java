package com.randal.aviana.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

And
if (Build.VERSION.SDK_INT >= 23) {
    if (!Settings.canDrawOverlays(MainActivity.this)) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 10);
    }
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 10) {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(MainActivity.this,"not granted",Toast.LENGTH_SHORT);
        }
    }
}

Usage:
    mDialog = new AlertDialogTwoBtn(this, "确定", "取消");
    mDialog.setTitle("节目时间冲突");
    mDialog.setContent("是否覆盖之前的节目");
    mDialog.setListener(new AlertDialogTwoBtn.OnTriggerListener() {
        @Override
        public void onConfirmClicked() {

        }

        @Override
        public void onCancelClicked() {

        }
    });
    mDialog.show();
*/

public class AlertDialogTwoBtn {

    private AlertDialog.Builder builder = null;
    private Dialog mDialog;
    private Context mContext;

    private TextView mTitle;
    private TextView mContent;

    private OnTriggerListener mListener;

    public AlertDialogTwoBtn(Context context,
                             String positive,
                             String negative) {
        mContext = context;
        if (builder == null) {
            LinearLayout mainLayout = new LinearLayout(mContext);
            mainLayout.setLayoutParams(new WindowManager.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mainLayout.setOrientation(LinearLayout.VERTICAL);

            mTitle = new TextView(mContext);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            titleParams.gravity = Gravity.CENTER;
            titleParams.setMargins(5, 0, 0, 10);
            mainLayout.addView(mTitle, titleParams);

            mContent = new TextView(mContext);
            LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            contentParams.gravity = Gravity.CENTER;
            contentParams.setMargins(0, 0, 0, 10);
            mainLayout.addView(mContent, contentParams);

            builder = new AlertDialog.Builder(mContext);
            builder.setView(mainLayout);
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mListener != null)
                        mListener.onConfirmClicked();
                    dismiss();
                }
            });
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mListener != null)
                        mListener.onCancelClicked();
                    dismiss();
                }
            });
            mDialog = builder.create();
            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mDialog.setCancelable(false);
        }
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * Set dialog title
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    /**
     * Set dialog content
     *
     * @param content
     */
    public void setContent(String content) {
        mContent.setText(content);
    }

    public void setListener(OnTriggerListener mListener) {
        this.mListener = mListener;
    }

    public interface OnTriggerListener{
        void onConfirmClicked();
        void onCancelClicked();
    }
}