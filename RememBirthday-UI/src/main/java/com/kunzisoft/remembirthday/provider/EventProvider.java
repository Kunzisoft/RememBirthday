package com.kunzisoft.remembirthday.provider;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kunzisoft.remembirthday.element.CalendarEvent;
import com.kunzisoft.remembirthday.element.Contact;

import org.joda.time.DateTime;

import java.util.Date;

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
            builder = ContentProviderOperation.newUpdate(
                    ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getId()));
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
            builder = ContentProviderOperation.newDelete(
                    ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getId()));
            return builder.build();
        } else {
            Log.e(TAG, "Can't delete the event, there is no id");
            return null;
        }
    }

    /**
     * Return each event from contact
     * @param contact Contact associated with events
     * @return Next event in the year or null if not fund
     */
    public static CalendarEvent getNextEventFromContact(Context context, Contact contact) {
        /* Two ways
            - Get events days of anniversary and filter with name (use for the first time)
            - Create links Event-Contact in custom table (may have bugs if event remove manually from calendar)
        */

        CalendarEvent calendarEvent = null;

        if(contact.hasBirthday()) {
            String[] projection = new String[] {
                    CalendarContract.Events._ID,
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DESCRIPTION,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND,
                    CalendarContract.Events.ALL_DAY};
            String where = CalendarContract.Events.TITLE + " LIKE ?";
            String[] whereParam = {
                    "'%" + contact.getName() + "%'"};

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(
                    CalendarProvider.getBirthdayAdapterUri(context, CalendarContract.Events.CONTENT_URI),
                    projection,
                    null,
                    null,
                    null);
            if(cursor != null) {
                cursor.moveToFirst();
                while (cursor.isAfterLast()) {
                    long id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.ORIGINAL_ID));
                    String title = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
                    String description = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
                    Date dateStart = new DateTime(
                            cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)))
                            .toDate();
                    Date dateEnd = new DateTime(
                            cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND)))
                            .toDate();
                    boolean allDay = cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ALL_DAY)) > 0;
                    if(allDay)
                        calendarEvent = new CalendarEvent(title, dateStart, true);
                    else
                        calendarEvent = new CalendarEvent(title, dateStart, dateEnd);
                    calendarEvent.setDescription(description);
                    calendarEvent.setId(id);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }

        return calendarEvent;
    }



}
