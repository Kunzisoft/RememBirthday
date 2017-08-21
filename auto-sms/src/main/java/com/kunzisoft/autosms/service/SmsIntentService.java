package com.kunzisoft.autosms.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.kunzisoft.autosms.database.AutoSmsDbHelper;

abstract public class SmsIntentService extends IntentService {

    protected long timestampCreated;

    public SmsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(getClass().getName(), "Handling intent");
        timestampCreated = intent.getLongExtra(AutoSmsDbHelper.COLUMN_TIMESTAMP_CREATED, 0L);
        if (timestampCreated == 0) {
            Log.i(getClass().getName(), "Cannot identify sms: no creation timestamp provided");
        }
    }
}
