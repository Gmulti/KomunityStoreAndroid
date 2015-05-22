package com.komunitystore;

import android.app.Activity;
import android.app.Application;

/**
 * Created by G3ck0z9 on 19/05/2015.
 */
public class KSApp extends Application {

    private Activity _currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Activity getCurrentActivity() {
        return _currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        _currentActivity = activity;
    }
}
