package com.kunzisoft.remembirthday.provider;

import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.util.Log;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.account.CalendarAccount;
import com.kunzisoft.remembirthday.preference.PreferencesHelper;

import java.util.ArrayList;

/**
 * Created by joker on 27/07/17.
 */

public class CalendarLoader {

    private static final String TAG = "CalendarLoader";
    private static String CALENDAR_COLUMN_NAME = "birthday_adapter";

    /**
     * Builds URI for Birthday Adapter based on account. Ensures that only the calendar of Birthday
     * Adapter is chosen.
     */
    public static Uri getBirthdayAdapterUri(Context context, Uri uri) {
        return uri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CalendarAccount.getAccountName(context))
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarAccount.getAccountType(context)).build();
    }

    /**
     * Updates calendar color
     */
    @SuppressWarnings("deprecation")
    public static void updateCalendarColor(Context context) {
        int color = PreferencesHelper.getCustomCalendarColor(context);
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = ContentUris.withAppendedId(
                CalendarLoader.getBirthdayAdapterUri(context, CalendarContract.Calendars.CONTENT_URI),
                getCalendar(context));

        Log.d(TAG, "Updating calendar color to " + color + " with uri " + uri.toString());

        ContentProviderClient client = contentResolver
                .acquireContentProviderClient(CalendarContract.AUTHORITY);
        if(client != null) {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Calendars.CALENDAR_COLOR, color);
            try {
                client.update(uri, values, null, null);
            } catch (RemoteException e) {
                Log.e(TAG, "Error while updating calendar color!", e);
            }

            if (android.os.Build.VERSION.SDK_INT < 24) {
                client.release();
            } else {
                client.close();
            }
        }
    }

    /**
     * Gets calendar id, when no calendar is present, create one!
     */
    public static long getCalendar(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        // Find the calendar if we've got one
        Uri calenderUri = getBirthdayAdapterUri(context, CalendarContract.Calendars.CONTENT_URI);

        // be sure to select the birthday calendar only (additionally to appendQueries in
        // getBirthdayAdapterUri for Android < 4)
        Cursor cursor = contentResolver.query(calenderUri, new String[]{BaseColumns._ID},
                CalendarContract.Calendars.ACCOUNT_NAME + " = ? AND " + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?",
                new String[]{CalendarAccount.getAccountName(context), CalendarAccount.getAccountType(context)}, null);

        try {
            if (cursor != null && cursor.moveToNext()) {
                return cursor.getLong(0);
            } else {
                ArrayList<ContentProviderOperation> operationList = new ArrayList<>();

                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newInsert(calenderUri);
                builder.withValue(CalendarContract.Calendars.ACCOUNT_NAME, CalendarAccount.getAccountName(context));
                builder.withValue(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarAccount.getAccountType(context));
                builder.withValue(CalendarContract.Calendars.NAME, CALENDAR_COLUMN_NAME);
                builder.withValue(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        context.getString(R.string.calendar_display_name));
                builder.withValue(CalendarContract.Calendars.CALENDAR_COLOR, PreferencesHelper.getCustomCalendarColor(context));
                //if (BuildConfig.DEBUG) {
                //    builder.withValue(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_EDITOR);
                //} else {
                builder.withValue(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_READ);
                //}
                builder.withValue(CalendarContract.Calendars.OWNER_ACCOUNT, CalendarAccount.getAccountName(context));
                builder.withValue(CalendarContract.Calendars.SYNC_EVENTS, 1);
                builder.withValue(CalendarContract.Calendars.VISIBLE, 1);
                operationList.add(builder.build());
                try {
                    contentResolver.applyBatch(CalendarContract.AUTHORITY, operationList);
                } catch (Exception e) {
                    Log.e(TAG, "getCalendar() failed", e);
                    return -1;
                }
                return getCalendar(context);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
    }

    public static void cleanTables(Context context, long calendarId) {

        // empty table
        // with additional selection of calendar id, necessary on Android < 4 to remove events only
        // from birthday calendar
        int delEventsRows = context.getContentResolver().delete(getBirthdayAdapterUri(context, CalendarContract.Events.CONTENT_URI),
                CalendarContract.Events.CALENDAR_ID + " = ?", new String[]{String.valueOf(calendarId)});
        Log.i(TAG, "Events of birthday calendar is now empty, deleted " + delEventsRows
                + " rows!");
        Log.i(TAG, "Reminders of birthday calendar is now empty!");
    }
}
