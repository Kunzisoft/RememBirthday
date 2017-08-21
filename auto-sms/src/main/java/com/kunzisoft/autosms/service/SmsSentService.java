package com.kunzisoft.autosms.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.kunzisoft.autosms.database.AutoSmsDbHelper;
import com.kunzisoft.autosms.model.AutoSms;
import com.kunzisoft.autosms.receiver.SmsSentReceiver;
import com.kunzisoft.autosms.receiver.WakefulBroadcastReceiver;

public class SmsSentService extends SmsIntentService {

    public SmsSentService() {
        super("SmsSentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        if (timestampCreated == 0) {
            return;
        }
        Log.i(getClass().getName(), "Notifying that sms " + timestampCreated + " is sent");
        AutoSms sms = AutoSmsDbHelper.getDbHelper(this).getAutoSmsById(timestampCreated);
        String errorId = "";
        String errorString = "";
        String title = "";//getString(R.string.notification_title_failure);
        String message = "";
        sms.setStatus(AutoSms.Status.FAILED);

        switch (intent.getIntExtra(SmsSentReceiver.RESULT_CODE, 0)) {
            case Activity.RESULT_OK:
                //title = getString(R.string.notification_title_success);
                //message = getString(R.string.notification_message_success, sms.getRecipientLookup());
                sms.setStatus(AutoSms.Status.SEND);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                errorId = AutoSms.ERROR_GENERIC;
                //errorString = getString(R.string.error_generic);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                errorId = AutoSms.ERROR_NO_SERVICE;
                //errorString = getString(R.string.error_no_service);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                errorId = AutoSms.ERROR_NULL_PDU;
                //errorString = getString(R.string.error_null_pdu);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                errorId = AutoSms.ERROR_RADIO_OFF;
                //errorString = getString(R.string.error_radio_off);
                break;
            default:
                errorId = AutoSms.ERROR_UNKNOWN;
                //errorString = getString(R.string.error_unknown);
                break;
        }
        if (errorId.length() > 0) {
            sms.setResult(errorId);
            //message = getString(R.string.notification_message_failure, sms.getRecipientLookup(), errorString);
        }
        AutoSmsDbHelper.getDbHelper(this).insert(sms);
        notify(this, title, message, sms.getDateCreated().getTime());
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void notify(Context context, String title, String message, long id) {
        /*
        Notification notification = NotificationManagerWrapper.getBuilder(context)
            .setIntent(new Intent(context, SmsListActivity.class))
            .setMessage(message)
            .setTitle(title)
            .build()
        ;
        new NotificationManagerWrapper(context).show(id, notification);
        */
    }
}
