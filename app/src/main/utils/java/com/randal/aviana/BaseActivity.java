package com.randal.aviana;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends Activity {
    protected void exitApplication() {
        ActivityCollector.finishAll();
    }

    protected <T extends View> T $(int id) {
        return (T) this.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        LogUtils.d(getClass().getSimpleName() + " onCreate()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}

class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (int i = activities.size()-1; i >= 0; --i) {
            if (!activities.get(i).isFinishing()) {
                activities.get(i).finish();
            }
        }
        activities.clear();
    }
}
