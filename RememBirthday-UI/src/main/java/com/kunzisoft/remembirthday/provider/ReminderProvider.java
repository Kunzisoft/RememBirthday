package com.kunzisoft.remembirthday.provider;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.provider.CalendarContract;
import android.util.Log;

import com.kunzisoft.remembirthday.element.Reminder;

/**
 * Created by joker on 28/07/17.
 */

public class ReminderProvider {

    private static final String TAG = "ReminderProvider";

    public static ContentProviderOperation insert(Context context, long eventId, Reminder reminder) {

        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(CalendarProvider.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI));

        builder.withValue(CalendarContract.Reminders.EVENT_ID, eventId);
        builder.withValue(CalendarContract.Reminders.MINUTES, reminder.getMinutesBeforeEvent());
        builder.withValue(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Log.d(TAG, "Add reminder : " + reminder);
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
        Log.d(TAG, "Add reminder : " + reminder);
        return builder.build();
    }
}
