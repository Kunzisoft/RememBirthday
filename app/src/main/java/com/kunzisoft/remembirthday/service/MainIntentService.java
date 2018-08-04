package com.kunzisoft.remembirthday.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.kunzisoft.remembirthday.account.BackgroundStatusHandler;
import com.kunzisoft.remembirthday.account.CalendarAccount;
import com.kunzisoft.remembirthday.preference.PreferencesHelper;
import com.kunzisoft.remembirthday.provider.CalendarLoader;

/**
 * An IntentServices queues incoming Intents and works them one by one.
 */
public class MainIntentService extends IntentService {

    /* extras that can be given by intent */
    public static final String EXTRA_MESSENGER = "messenger";

    /* possible actions */
    public static final String ACTION_MANUAL_COMPLETE_SYNC = "MANUAL_SYNC";
    public static final String ACTION_CLEAN = "CLEAN";
    public static final String ACTION_CHANGE_COLOR = "CHANGE_COLOR";

    private Messenger mMessenger;

    public MainIntentService() {
        super("BirthdayAdapterMainIntentService");
    }

    /**
     * Start service with action, while executing, show progress
     */
    public static void startServiceAction(Context context, String action) {
        startServiceAction(context, action, new BackgroundStatusHandler(null));
    }

    public static void startServiceAction(Context context, String action, Handler handler) {
        Intent intent = new Intent(context, MainIntentService.class);

        // Create a new Messenger for the communication back
        if (handler != null) {
            Messenger messenger = new Messenger(handler);
            intent.putExtra(MainIntentService.EXTRA_MESSENGER, messenger);
        }
        intent.setAction(action);

        // start service with intent
        context.startService(intent);
    }

    /**
     * The IntentService calls this method from the default worker thread with the intent that
     * started the service. When this method returns, IntentService stops the service, as
     * appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Only if calendar is active
        if(PreferencesHelper.isCustomCalendarActive(this)) {

            Bundle extras = intent.getExtras();
            if (extras == null) {
                Log.e(getClass().getSimpleName(), "Extras bundle is null!");
            } else if (extras.containsKey(EXTRA_MESSENGER)) {
                mMessenger = (Messenger) extras.get(EXTRA_MESSENGER);
            }
            setProgressCircleWithHandler(true);

            if (intent.getAction() == null) {
                Log.e(getClass().getSimpleName(), "Intent must contain an action!");
                return;
            }
            String action = intent.getAction();

            // execute action
            switch (action) {
                case ACTION_CHANGE_COLOR:
                    // update calendar color if enabled
                    if (CalendarAccount.isAccountActivated(this)) {
                        CalendarLoader.updateCalendarColor(this);
                    }
                    break;
                case ACTION_MANUAL_COMPLETE_SYNC:
                    // perform blocking sync
                    CalendarSyncAdapterService.performSync(this);
                    break;
            }
            setProgressCircleWithHandler(false);
        }
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

