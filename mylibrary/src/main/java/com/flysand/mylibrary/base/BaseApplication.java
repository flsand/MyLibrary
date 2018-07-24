package com.flysand.mylibrary.base;

import android.app.Application;

import com.flysand.mylibrary.crash.CrashHandler;

/**
 * Created by FlySand on 2017\7\24 0024.
 */

public class BaseApplication extends Application {

    protected static BaseApplication w;

    public static <T> T getInstance() {
        return ((T) w);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (w == null) {
            synchronized (BaseApplication.class) {
                if (w == null) {
                    w = this;
                    ActivityLifecycle.getInstance(this);
                    CrashHandler.getInstance(w);
                }
            }
        }
    }
}
