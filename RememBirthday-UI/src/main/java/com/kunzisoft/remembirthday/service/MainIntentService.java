package com.kunzisoft.remembirthday.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.kunzisoft.remembirthday.account.BackgroundStatusHandler;
import com.kunzisoft.remembirthday.account.CalendarAccount;

/**
 * An IntentServices queues incoming Intents and works them one by one.
 */
public class MainIntentService extends IntentService {

    /* extras that can be given by intent */
    public static final String EXTRA_MESSENGER = "messenger";

    /* possible actions */
    public static final String ACTION_MANUAL_COMPLETE_SYNC = "MANUAL_SYNC";
    public static final String ACTION_CHANGE_COLOR = "CHANGE_COLOR";

    private Messenger mMessenger;

    public MainIntentService() {
        super("BirthdayAdapterMainIntentService");
    }

    /**
     * The IntentService calls this method from the default worker thread with the intent that
     * started the service. When this method returns, IntentService stops the service, as
     * appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.e(getClass().getSimpleName(), "Extras bundle is null!");
            return;
        }

        if (intent.getAction() == null) {
            Log.e(getClass().getSimpleName(), "Intent must contain an action!");
            return;
        }

        if (extras.containsKey(EXTRA_MESSENGER)) {
            mMessenger = (Messenger) extras.get(EXTRA_MESSENGER);
        }

        String action = intent.getAction();

        setProgressCircleWithHandler(true);

        // execute action
        switch (action) {
            case ACTION_CHANGE_COLOR:
                // update calendar color if enabled
                if (CalendarAccount.isAccountActivated(this)) {
                    CalendarSyncAdapterService.updateCalendarColor(this);
                }
                break;
            case ACTION_MANUAL_COMPLETE_SYNC:
                // perform blocking sync
                CalendarSyncAdapterService.performSync(this);
                break;
        }

        setProgressCircleWithHandler(false);
    }

    private void setProgressCircleWithHandler(boolean value) {
        Message msg = Message.obtain();

        if (value) {
            msg.what = BackgroundStatusHandler.BACKGROUND_STATUS_HANDLER_ENABLE;
        } else {
            msg.what = BackgroundStatusHandler.BACKGROUND_STATUS_HANDLER_DISABLE;
        }

        if (mMessenger != null) {
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                Log.w(getClass().getSimpleName(), "Exception sending message, Is handler present?", e);
            }
        }
    }

}

