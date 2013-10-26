package com.team5hinf.support;
import java.util.ArrayList;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import edu.neu.android.wocketslib.ApplicationManager;

/**
 * The entry of the whole application.
 *
 * @author bigbug
 */
public class DemoAppManager extends ApplicationManager {

    public static final String TAG = "DemoAppManager";

    @Override
    public void onCreate() {
        super.onCreate();

        DemoGlobals.initGlobals(getAppContext());
    }

    public static AssetManager getAppAssets() {
        return getAppContext().getAssets();
    }

    public static Resources getAppResources() {
        return getAppContext().getResources();
    }

    private ArrayList<Activity> mActivityList = new ArrayList<Activity>();

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void killActivity(Activity activity) {
        activity.finish();
        mActivityList.remove(activity);
    }

    public void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    public void killAllActivities() {
        for (Activity activity : mActivityList) {
            activity.finish();
        }
        mActivityList.clear();
    }
}