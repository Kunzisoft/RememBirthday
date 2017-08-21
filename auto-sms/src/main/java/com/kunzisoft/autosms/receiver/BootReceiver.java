package com.kunzisoft.autosms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kunzisoft.autosms.Scheduler;
import com.kunzisoft.autosms.database.AutoSmsDbHelper;
import com.kunzisoft.autosms.model.AutoSms;


import java.util.Iterator;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "Rescheduling all the sms");
        String action = intent.getAction();
        if (TextUtils.isEmpty(action) || !action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            return;
        }
        List<AutoSms> pendingSms = AutoSmsDbHelper.getDbHelper(context).getListAutoSmsByStatus(AutoSms.Status.PENDING);
        Iterator<AutoSms> i = pendingSms.iterator();

        // TODO preference notifications

        Scheduler scheduler = new Scheduler(context);
        while (i.hasNext()) {
            scheduler.schedule(i.next(), true);
        }
    }
}
