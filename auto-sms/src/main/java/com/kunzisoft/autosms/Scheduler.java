package com.kunzisoft.autosms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.kunzisoft.autosms.database.AutoSmsDbHelper;
import com.kunzisoft.autosms.model.AutoSms;
import com.kunzisoft.autosms.receiver.ReminderReceiver;
import com.kunzisoft.autosms.receiver.SmsSenderReceiver;

import java.text.DateFormat;

public class Scheduler {

    static private final long HOUR = 1000L*60L*60L;

    private Context context;
    private AlarmManager alarmManager;

    public Scheduler(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void schedule(AutoSms sms, boolean remindersActive) {
        if (null == alarmManager) {
            return;
        }
        Log.i(getClass().getName(), "Scheduling sms to " + DateFormat.getDateTimeInstance().format(sms.getDateScheduled()));
        setAlarm(sms.getDateScheduled().getTime(), getAlarmPendingIntent(sms.getDateCreated().getTime(), SmsSenderReceiver.class));
        if (remindersActive) {
            setAlarm(sms.getDateScheduled().getTime() - HOUR, getAlarmPendingIntent(sms.getDateCreated().getTime(), ReminderReceiver.class));
        }
    }

    public void unschedule(long timestampCreated) {
        if (null == alarmManager) {
            return;
        }
        alarmManager.cancel(getAlarmPendingIntent(timestampCreated, SmsSenderReceiver.class));
        alarmManager.cancel(getAlarmPendingIntent(timestampCreated, ReminderReceiver.class));
    }

    private void setAlarm(long timestamp, PendingIntent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, intent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, intent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, intent);
        }
    }

    private PendingIntent getAlarmPendingIntent(long timestampCreated, Class receiverClass) {
        Intent intent = new Intent(context, receiverClass);
        intent.putExtra(AutoSmsDbHelper.COLUMN_TIMESTAMP_CREATED, timestampCreated);
        return PendingIntent.getBroadcast(
                context,
                (int) (timestampCreated / 1000L),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT & Intent.FILL_IN_DATA
        );
    }
}
