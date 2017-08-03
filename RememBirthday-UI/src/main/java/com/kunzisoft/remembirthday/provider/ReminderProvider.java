package com.kunzisoft.remembirthday.provider;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;

import com.kunzisoft.remembirthday.element.CalendarEvent;
import com.kunzisoft.remembirthday.element.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 28/07/17.
 */

public class ReminderProvider {

    private static final String TAG = "ReminderProvider";

    public static List<Reminder> getRemindersFromEvent(Context context, CalendarEvent calendarEvent) {
        List<Reminder> reminderList = new ArrayList<>();

        String[] projection = new String[] {
                CalendarContract.Reminders.EVENT_ID,
                CalendarContract.Reminders.MINUTES,
                CalendarContract.Reminders.METHOD};
        String where = CalendarContract.Reminders.EVENT_ID + "=?";
        String[] whereParam = {Long.toString(calendarEvent.getId())};

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                CalendarProvider.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI),
                projection,
                where,
                whereParam,
                null);
        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Reminder reminder = new Reminder(
                        calendarEvent.getDate(),
                        cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES)));
                reminderList.add(reminder);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return reminderList;
    }

    // TODO mutualize
    public static ContentProviderOperation insert(Context context, long eventId, Reminder reminder) {

        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(CalendarProvider.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI));

        builder.withValue(CalendarContract.Reminders.EVENT_ID, eventId);
        builder.withValue(CalendarContract.Reminders.MINUTES, reminder.getMinutesBeforeEvent());
        builder.withValue(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        return builder.build();
    }


    public static ContentProviderOperation insert(Context context, Reminder reminder, int backref) {
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(CalendarProvider.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI));

        /*
         * add reminder to last added event identified by backRef
         * see http://stackoverflow.com/questions/4655291/semantics-of-
         * withvaluebackreference
         */
        builder.withValueBackReference(CalendarContract.Reminders.EVENT_ID, backref);
        builder.withValue(CalendarContract.Reminders.MINUTES, reminder.getMinutesBeforeEvent());
        builder.withValue(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        return builder.build();
    }
}
