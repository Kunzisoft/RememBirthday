package com.kunzisoft.remembirthday.account;

import android.content.Context;
import android.os.Handler;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 25/07/17.
 */

public class CalendarAccount {

    private CalendarAccount() {}

    public static AccountResolver getAccount(Context context) {
        return getAccount(context, null);
    }

    public static AccountResolver getAccount(Context context, Handler handler) {
        return new AccountResolver(context,
                    getAccountName(context),
                    getAccountType(context),
                    getAccountAuthority(context),
                    handler);
    }

    public static boolean isAccountActivated(Context context) {
        return AccountResolver.isAccountActivated(context,
                getAccountType(context),
                getAccountName(context));
    }

    public static String getAccountName(Context context) {
        return context.getString(R.string.account_name);
    }

    public static String getAccountType(Context context) {
        return context.getString(R.string.account_type);
    }

    public static String getAccountAuthority(Context context) {
        return context.getString(R.string.account_authority);
    }
}
