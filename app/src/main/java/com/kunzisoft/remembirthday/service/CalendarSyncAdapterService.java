package com.kunzisoft.remembirthday.service;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.kunzisoft.remembirthday.account.AccountResolver;
import com.kunzisoft.remembirthday.account.CalendarAccount;
import com.kunzisoft.remembirthday.provider.EventLoader;

@SuppressLint("NewApi")
public class CalendarSyncAdapterService extends Service {

    private static final String TAG = "CalendarSyncService";

    public CalendarSyncAdapterService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CalendarSyncAdapter().getSyncAdapterBinder();
    }

    private static void performSync(Context context, Account account, Bundle extras,
                                    String authority, ContentProviderClient provider, SyncResult syncResult)
            throws OperationCanceledException {
        performSync(context);
    }

    public static void performSync(Context context) {
        Log.d(TAG, "Starting sync...");
        EventLoader.saveEventsIfNotExistsFromAllContactWithBirthday(context);
    }


    private class CalendarSyncAdapter extends AbstractThreadedSyncAdapter {

        CalendarSyncAdapter() {
            super(CalendarSyncAdapterService.this, true);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority,
                                  ContentProviderClient provider, SyncResult syncResult) {
            try {
                CalendarSyncAdapterService.performSync(CalendarSyncAdapterService.this, account, extras, authority,
                        provider, syncResult);
            } catch (OperationCanceledException e) {
                Log.e(getClass().getSimpleName(), "OperationCanceledException", e);
            }
        }

        @Override
        public void onSecurityException(Account account, Bundle extras, String authority, SyncResult syncResult) {
            super.onSecurityException(account, extras, authority, syncResult);

            // contact or calendar permission has been revoked -> simply remove account
            AccountResolver accountResolver = CalendarAccount.getAccount(CalendarSyncAdapterService.this, null);
            accountResolver.removeAccount();
        }
    }
}
