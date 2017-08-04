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
import org.joda.time.DateTimeZone;

import java.util.Date;
import java.util.TimeZone;

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
        if(event.isAllDay()) {
            // ALL_DAY events must be UTC
            DateTime dateTimeStartUTC = new DateTime(event.getDateStart()).withZoneRetainFields(DateTimeZone.UTC);
            DateTime dateTimeStopUTC = new DateTime(event.getDateStop()).withZoneRetainFields(DateTimeZone.UTC);
            builder.withValue(CalendarContract.Events.DTSTART, dateTimeStartUTC.toDate().getTime());
            builder.withValue(CalendarContract.Events.DTEND, dateTimeStopUTC.toDate().getTime());
            builder.withValue(CalendarContract.Events.EVENT_TIMEZONE, "UTC");
            builder.withValue(CalendarContract.Events.ALL_DAY, 1);
        } else {
            builder.withValue(CalendarContract.Events.DTSTART, event.getDateStart().getTime());
            builder.withValue(CalendarContract.Events.DTEND, event.getDateStop().getTime());
            builder.withValue(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
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
                    CalendarContract.Events.EVENT_TIMEZONE,
                    CalendarContract.Events.ALL_DAY};
            /*
             * Get newt event who have an all day in the day of the event with name of contact in title
             */
            String where = CalendarContract.Events.DTSTART + "=?" +
                    " AND " + CalendarContract.Events.TITLE + " LIKE ?";
            String[] whereParam = {
                    String.valueOf(new DateTime(contact.getNextBirthday())
                            .withZoneRetainFields(DateTimeZone.UTC)
                            .toDateTime().toDate().getTime()),
                    "%" + contact.getName() + "%"};
            // TODO better retrieve

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(
                    CalendarProvider.getBirthdayAdapterUri(context, CalendarContract.Events.CONTENT_URI),
                    projection,
                    where,
                    whereParam,
                    null);
            if(cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    long id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID));
                    String title = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
                    String description = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION));
                    boolean allDay = cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ALL_DAY)) > 0;
                    if(allDay) {
                        Date dateStart = new DateTime(
                                cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)),
                                DateTimeZone.forID(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE))))
                                .withZone(DateTimeZone.getDefault())
                                .toDate();
                        calendarEvent = new CalendarEvent(title, dateStart, true);
                    } else {
                        Date dateStart = new DateTime(
                                cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)),
                                DateTimeZone.forID(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE))))
                                .withZone(DateTimeZone.getDefault())
                                .toDate();
                        Date dateEnd = new DateTime(
                                cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND)),
                                DateTimeZone.forID(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE))))
                                .withZone(DateTimeZone.getDefault())
                                .toDate();
                        calendarEvent = new CalendarEvent(title, dateStart, dateEnd);
                    }
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
