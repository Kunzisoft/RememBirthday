package com.kunzisoft.remembirthday.provider;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.kunzisoft.remembirthday.element.CalendarEvent;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by joker on 27/07/17.
 */

public class EventProvider {

    private static final String TAG = "EventProvider";

    /**
     * Utility method for add values in Builder
     * @param builder ContentProviderOperation.Builder
     * @param event Event to add
     */
    private static void assignValuesInBuilder(ContentProviderOperation.Builder builder, CalendarEvent event) {
        builder.withValue(CalendarContract.Events.DTSTART, event.getDateStart().getTime());
        builder.withValue(CalendarContract.Events.DTEND, event.getDateStop().getTime());
        if(event.isAllDay()) {
            // ALL_DAY events must be UTC
            builder.withValue(CalendarContract.Events.EVENT_TIMEZONE, "UTC");
            builder.withValue(CalendarContract.Events.ALL_DAY, 1);
        }
        builder.withValue(CalendarContract.Events.TITLE, event.getTitle());
    }

    /**
     * Get a new ContentProviderOperation to insert an event
     */
    public static ContentProviderOperation insert(Context context, long calendarId,
                                                  CalendarEvent event, @Nullable Contact contact) {
        ContentProviderOperation.Builder builder;

        builder = ContentProviderOperation.newInsert(CalendarProvider.getBirthdayAdapterUri(context, CalendarContract.Events.CONTENT_URI));
        builder.withValue(CalendarContract.Events.CALENDAR_ID, calendarId);
        assignValuesInBuilder(builder, event);

        builder.withValue(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED);

        /*
         * Enable reminders for this event
         * Note: Needs to be explicitly set on Android < 4 to enable reminders
         */
        builder.withValue(CalendarContract.Events.HAS_ALARM, 1);

        /*
         * Set availability to free.
         * Note: HTC calendar (4.0.3 Android + HTC Sense 4.0) will show a conflict with other events
         * if availability is not set to free!
         */
        if (Build.VERSION.SDK_INT >= 14) {
            builder.withValue(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
        }

        // add button to open contact
        if (Build.VERSION.SDK_INT >= 16 && contact != null && contact.getLookUpKey() != null) {
            builder.withValue(CalendarContract.Events.CUSTOM_APP_PACKAGE, context.getPackageName());
            Uri contactLookupUri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_LOOKUP_URI, contact.getLookUpKey());
            builder.withValue(CalendarContract.Events.CUSTOM_APP_URI, contactLookupUri.toString());
        }

        Log.d(TAG, "Add event : " + event);
        return builder.build();
    }

    /**
     * Update the specific event, id must be specified
     * @param event Event to update
     * @return ContentProviderOperation to apply or null if no id
     */
    public static ContentProviderOperation update(CalendarEvent event) {

        if(event.hasId()) {
            ContentProviderOperation.Builder builder;
            builder = ContentProviderOperation.newUpdate(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getId()));
            // Push values
            assignValuesInBuilder(builder, event);
            return builder.build();
        } else {
            Log.e(TAG, "Can't update the event, there is no id");
            return null;
        }
    }

    /**
     * Delete the specific event, id must be specified
     * @param event Event to delete
     * @return ContentProviderOperation to apply or null if no id
     */
    public static ContentProviderOperation delete(CalendarEvent event) {

        if(event.hasId()) {
            ContentProviderOperation.Builder builder;
            builder = ContentProviderOperation.newDelete(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getId()));
            return builder.build();
        } else {
            Log.e(TAG, "Can't delete the event, there is no id");
            return null;
        }
    }

    public static List<CalendarEvent> getEventFromContact(Contact contact) {
        return null;
    }



}
