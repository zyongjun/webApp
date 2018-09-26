package com.joe.frame;

import android.app.Application;
import android.util.DisplayMetrics;

public class WebApplication extends Application{

    private static WebApplication INSTANCE = null;

    private DisplayMetrics displayMetrics;

    public static WebApplication getInstance() {
        return INSTANCE;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public void setDisplayMetrics(DisplayMetrics displayMetrics) {
        this.displayMetrics = displayMetrics;
    }
}
