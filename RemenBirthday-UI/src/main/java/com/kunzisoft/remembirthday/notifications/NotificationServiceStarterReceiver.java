package com.kunzisoft.remembirthday.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by joker on 17/04/17.
 */

public class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO changement de fuseau horaire
        //NotificationEventReceiver.setupAlarm(context);
    }
}
