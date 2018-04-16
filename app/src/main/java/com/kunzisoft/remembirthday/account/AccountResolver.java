package com.kunzisoft.remembirthday.account;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.kunzisoft.remembirthday.service.MainIntentService;

/**
 * Created by joker on 25/07/17.
 */
public class AccountResolver {

    private Context context;
    private Handler backgroundStatusHandler;
    private String name;
    private String type;
    private Account account;
    private String authority;

    public AccountResolver(Context context,
                           String name,
                           String type,
                           String authority,
                           Handler handler) {
        this.context = context;
        this.name = name;
        this.type = type;
        this.account = new Account(name, type);
        this.authority = authority;
        this.backgroundStatusHandler = handler;
    }

    public AccountResolver(Context context,
                           String accountName,
                           String type,
                           String authority) {
        this(context, accountName, type, authority, null);
    }

    /**
     * Add account for Birthday Adapter to Android system
     */
    public Bundle addAccountAndSync() {
        Log.d(getClass().getSimpleName(), "Adding calendar account : " + account.name);

        // enable automatic sync once per day
        ContentResolver.setSyncAutomatically(account, authority, true);
        ContentResolver.setIsSyncable(account, type, 1);

        // add periodic sync interval once per day
        long freq = AlarmManager.INTERVAL_DAY;
        ContentResolver.addPeriodicSync(account, type, new Bundle(), freq);

        AccountManager accountManager = AccountManager.get(context);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);

            // Force a sync! Even when background sync is disabled, this will force one sync!
            manualSync();

            return result;
        } else {
            return null;
        }
    }

    /**
     * Remove account from Android system
     */
    @SuppressWarnings("deprecation")
    public boolean removeAccount() {
        Log.d(getClass().getSimpleName(), "Removing account : " + account.name);

        AccountManager accountManager = AccountManager.get(context);
        // remove account
        AccountManagerFuture accountManagerFuture;
        if(android.os.Build.VERSION.SDK_INT < 23) {
            accountManagerFuture = accountManager.removeAccount(account, null, null);
        } else {
            accountManagerFuture = accountManager.removeAccount(account, null, null, null);
        }
        if (accountManagerFuture.isDone()) {
            try {
                accountManagerFuture.getResult();
                return true;
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Problem while removing account!", e);
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Force a manual sync now!
     */
    public void manualSync() {
        MainIntentService.startServiceAction(context,
                MainIntentService.ACTION_MANUAL_COMPLETE_SYNC,
                backgroundStatusHandler);
    }

    /**
     * Checks whether the account is enabled or not
     */
    public static boolean isAccountActivated(Context context, String type, String name) {
        AccountManager accountManager = AccountManager.get(context);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Account[] availableAccounts = accountManager.getAccountsByType(type);
        for (Account currentAccount : availableAccounts) {
            if (currentAccount.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Account getAccount() {
        return account;
    }

    public String getAuthority() {
        return authority;
    }
}
