package com.kunzisoft.remembirthday;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Class for initialize library at startup
 */
public class RememBirthday extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Date library
        JodaTimeAndroid.init(this);
    }
}
