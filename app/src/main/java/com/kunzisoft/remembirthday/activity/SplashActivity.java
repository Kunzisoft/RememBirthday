package com.kunzisoft.remembirthday.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * SplashScreen activity, only shows the icon of app and call the main activity
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BuddyActivity.class);
        startActivity(intent);
        finish();
    }
}