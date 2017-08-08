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

    public static ContentProviderOperation insert(Context context, long eventId, Reminder reminder) {
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(CalendarLoader.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI));
        builder.withValue(CalendarContract.Reminders.EVENT_ID, eventId);
        return insert(builder, reminder);
    }


    public static ContentProviderOperation insert(Context context, Reminder reminder, int backref) {
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(CalendarLoader.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI));
        /*
         * add reminder to last added event identified by backRef
         * see http://stackoverflow.com/questions/4655291/semantics-of-
         * withvaluebackreference
         */
        builder.withValueBackReference(CalendarContract.Reminders.EVENT_ID, backref);
        return insert(builder, reminder);
    }

    private static ContentProviderOperation insert(ContentProviderOperation.Builder builder, Reminder reminder) {
        builder.withValue(CalendarContract.Reminders.MINUTES, reminder.getMinutesBeforeEvent());
        builder.withValue(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        return builder.build();
    }

    public static ContentProviderOperation update(Context context, long eventId, Reminder reminder) {
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newUpdate(CalendarLoader.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI))
            .withSelection(CalendarContract.Reminders._ID + " =?"
                    + " AND " + CalendarContract.Reminders.EVENT_ID + " =?"
                , new String[]{String.valueOf(reminder.getId()),
                    String.valueOf(eventId)})
            .withValue(CalendarContract.Reminders.MINUTES, reminder.getMinutesBeforeEvent());
        return  builder.build();
    }

    public static ContentProviderOperation updateWithUnknownId(Context context, long eventId, Reminder reminder, int newMinutes) {
        // TODO update
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newUpdate(CalendarLoader.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI))
                .withSelection(CalendarContract.Reminders.EVENT_ID + " =?"
                                + " AND " + CalendarContract.Reminders.MINUTES + " =?"
                        , new String[]{String.valueOf(eventId),
                                String.valueOf(reminder.getMinutesBeforeEvent())})
                .withValue(CalendarContract.Reminders.MINUTES, newMinutes);
        return  builder.build();
    }

    public static ContentProviderOperation delete(Context context, long eventId, Reminder reminder) {
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newDelete(CalendarLoader.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI))
                .withSelection(CalendarContract.Reminders._ID + " =?"
                                + " AND " + CalendarContract.Reminders.EVENT_ID + " =?"
                        , new String[]{String.valueOf(reminder.getId()),
                                String.valueOf(eventId)});
        return  builder.build();
    }

    public static ContentProviderOperation deleteWithUnknownId(Context context, long eventId, Reminder reminder) {
        // TODO delete
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newDelete(CalendarLoader.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI))
                .withSelection(CalendarContract.Reminders.EVENT_ID + " =?"
                                + " AND " + CalendarContract.Reminders.MINUTES + " =?"
                        , new String[]{String.valueOf(eventId),
                                String.valueOf(reminder.getMinutesBeforeEvent())});
        return  builder.build();
    }

    public static ContentProviderOperation deleteAll(Context context, long eventId) {
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newDelete(CalendarLoader.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI))
                .withSelection(CalendarContract.Reminders.EVENT_ID + " =?"
                        , new String[]{String.valueOf(eventId)});
        return  builder.build();
    }
}
