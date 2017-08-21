package com.kunzisoft.autosms.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;

import com.kunzisoft.autosms.CalendarResolver;
import com.kunzisoft.autosms.Scheduler;
import com.kunzisoft.autosms.database.AutoSmsDbHelper;
import com.kunzisoft.autosms.model.AutoSms;
import com.kunzisoft.autosms.receiver.SmsDeliveredReceiver;
import com.kunzisoft.autosms.receiver.SmsSentReceiver;
import com.kunzisoft.autosms.receiver.WakefulBroadcastReceiver;

import java.util.ArrayList;

public class SmsSenderService extends SmsIntentService {

    public SmsSenderService() {
        super("SmsSenderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        if (timestampCreated == 0) {
            return;
        }
        AutoSms sms = AutoSmsDbHelper.getDbHelper(this).getAutoSmsById(timestampCreated);
        Log.i(getClass().getName(), "Sending sms " + timestampCreated);
        sendSms(sms, false);
        String recurringMode = sms.getRecurringMode();
        if (!TextUtils.isEmpty(recurringMode) && !recurringMode.equals(CalendarResolver.RECURRING_NO)) {
            Log.i(getClass().getName(), "Scheduling next sms");
            scheduleNextSms(sms);
        }
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendSms(AutoSms sms, boolean deliveryReports) {
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<>();
        PendingIntent sentPendingIntent = getPendingIntent(sms.getDateCreated().getTime(), SmsSentReceiver.class);
        PendingIntent deliveredPendingIntent = getPendingIntent(sms.getDateCreated().getTime(), SmsDeliveredReceiver.class);

        SmsManager smsManager = getSmsManager(sms.getSubscriptionId());
        ArrayList<String> smsMessage = smsManager.divideMessage(sms.getMessage());
        for (int i = 0; i < smsMessage.size(); i++) {
            sentPendingIntents.add(i, sentPendingIntent);
            if (deliveryReports) {
                deliveredPendingIntents.add(i, deliveredPendingIntent);
            }
        }
        smsManager.sendMultipartTextMessage(
            sms.getRecipientPhoneNumber(),
            null,
            smsMessage,
            sentPendingIntents,
            deliveryReports ? deliveredPendingIntents : null
        );
    }

    private PendingIntent getPendingIntent(long smsId, Class receiverClass) {
        Intent intent = new Intent(this, receiverClass);
        intent.setAction(Long.toString(smsId));
        intent.putExtra(AutoSmsDbHelper.COLUMN_TIMESTAMP_CREATED, smsId);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    private SmsManager getSmsManager(int subscriptionId) {
        SmsManager smsManager = SmsManager.getDefault();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            return smsManager;
        }
        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (null == subscriptionManager) {
            return smsManager;
        }
        if (null == subscriptionManager.getActiveSubscriptionInfo(subscriptionId)) {
            return smsManager;
        }
        return SmsManager.getSmsManagerForSubscriptionId(subscriptionId);
    }

    private void scheduleNextSms(AutoSms sms) {
        new CalendarResolver().initCalendar(sms.getDateScheduled()).setRecurringMode(sms.getRecurringMode()).advance();
        AutoSmsDbHelper.getDbHelper(this).insert(sms);
        new Scheduler(getApplicationContext()).schedule(sms, false);
    }
}
