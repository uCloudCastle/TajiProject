package com.randal.aviana;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

/**
 * Aviana BaseApplication
 * Usage:
 * Add [android:name="com.randal.aviana.BaseApplication"] In AndroidManifest.xml
 *
 * Some Utils You should know they are already exits:
 * android.text.TextUtils;
 * android.webkit.URLUtil;
 * android.text.format.DateUtils;
 */

public class BaseApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static String getProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
