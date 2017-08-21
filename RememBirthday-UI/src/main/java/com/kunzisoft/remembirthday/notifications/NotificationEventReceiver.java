package com.kunzisoft.remembirthday.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.kunzisoft.remembirthday.element.Contact;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by joker on 17/04/17.
 */
public class NotificationEventReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_START_NOTIFICATION_SERVICE = "com.kunzisoft.remembirthday.ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "com.kunzisoft.remembirthday.ACTION_DELETE_NOTIFICATION";
    private static final String EXTRA_CONTACT_NOTIFICATION = "EXTRA_CONTACT_NOTIFICATION_RECEIVER";

    public static void setupAlarm(Context context, Contact contact) {

        Log.e("NotificationEvtReceiver", contact.getBirthday().getNextAnniversary().toString());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntent(context, contact);
        //TODO Get Anniversary bug just last is called
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                getTriggerAt(contact.getBirthday().getNextAnniversary()),
                alarmIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent serviceIntent = null;
        if (ACTION_START_NOTIFICATION_SERVICE.equals(action)) {
            Contact contact = intent.getParcelableExtra(EXTRA_CONTACT_NOTIFICATION);
            Log.i(getClass().getSimpleName(), "onReceive from alarm, starting notification service");
            serviceIntent = NotificationIntentService.createIntentStartNotificationService(context, contact);
        } else if (ACTION_DELETE_NOTIFICATION.equals(action)) {
            Log.i(getClass().getSimpleName(), "onReceive deleteById notification action, starting notification service to handle deleteById");
            serviceIntent = NotificationIntentService.createIntentDeleteNotification(context);
        }

        if (serviceIntent != null) {
            startWakefulService(context, serviceIntent);
        }
    }

    private static long getTriggerAt(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        //calendar.add(Calendar.HOUR, NOTIFICATIONS_INTERVAL_IN_HOURS);
        return calendar.getTimeInMillis();
    }

    private static PendingIntent getStartPendingIntent(Context context, Contact contact) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
        intent.putExtra(EXTRA_CONTACT_NOTIFICATION, contact);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
