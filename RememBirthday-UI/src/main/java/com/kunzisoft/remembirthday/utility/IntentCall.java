package com.kunzisoft.remembirthday.utility;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kunzisoft.remembirthday.BuildConfig;

import java.util.Calendar;

/**
 * Utility class who manage the calls of external apps
 */
public class IntentCall {

    private IntentCall() {}

    /**
     * Open calendar application at specific time
     * @param context Context to call
     * @param date Date to show in calendar
     */
    public static void openCalendarAt(android.content.Context context, java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, calendar.getTimeInMillis());
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        try {
            context.startActivity(intent);
        } catch(ActivityNotFoundException e) {
            Log.e(context.getClass().getSimpleName(), e.toString());
        }
    }

    /**
     * Open phone app for a call
     * @param context Context to call
     * @param phoneNumber Phone Number as a String
     */
    public static void openCallApp(Context context, String phoneNumber) {
        Intent callIntent = new Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + phoneNumber));
        if (callIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(callIntent);
        }
    }

    /**
     * Open SMS app
     * @param context Context to call
     * @param phoneNumber Phone Number as a String
     * @param defaultMessage Default message, can be null
     */
    public static void openSMSApp(Context context, String phoneNumber, @Nullable String defaultMessage) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        if(defaultMessage != null)
            intent.putExtra("sms_body", defaultMessage);
        context.startActivity(intent);
    }

    /**
     * Open SMS app
     * @param context Context to call
     * @param phoneNumber Phone Number as a String
     */
    public static void openSMSApp(Context context, String phoneNumber) {
        openSMSApp(context, phoneNumber, null);
    }

    /**
     * Open the specific page to make a contribution
     * @param context Context to call
     */
    public static void openContributionPage(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Constants.URL_PARTICIPATION));
        context.startActivity(intent);
    }

    /**
     * Open the store for pro version
     * @param context Context to call
     */
    public static void openStoreProApplication(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + BuildConfig.PRO_APPLICATION_ID)));
        } catch (android.content.ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + BuildConfig.PRO_APPLICATION_ID)));
        }
    }

}
