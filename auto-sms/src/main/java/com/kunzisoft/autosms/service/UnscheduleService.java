package com.kunzisoft.autosms.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kunzisoft.autosms.Scheduler;
import com.kunzisoft.autosms.database.AutoSmsDbHelper;

public class UnscheduleService extends SmsIntentService {

    public UnscheduleService() {
        super("UnscheduleService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        if (timestampCreated == 0) {
            return;
        }
        Log.i(getClass().getName(), "Removing sms " + timestampCreated);
        unschedule(getApplicationContext(), timestampCreated);
    }

    static private void unschedule(Context context, long timestampCreated) {
        new Scheduler(context).unschedule(timestampCreated);
        AutoSmsDbHelper.getDbHelper(context).deleteById(timestampCreated);
        Log.i(UnscheduleService.class.getName(), "Deleting notification with id " + timestampCreated);
        //new NotificationManagerWrapper(context).cancel(id);
    }
}
