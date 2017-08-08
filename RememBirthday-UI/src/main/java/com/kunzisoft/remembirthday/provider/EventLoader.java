package com.kunzisoft.remembirthday.provider;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.util.Log;

import com.kunzisoft.remembirthday.element.CalendarEvent;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.element.EventWithoutYear;
import com.kunzisoft.remembirthday.element.Reminder;
import com.kunzisoft.remembirthday.utility.QueryTool;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by joker on 08/08/17.
 */

public class EventLoader {

    private final static String TAG = "EventLoader";


    public synchronized static List<CalendarEvent> getEventsSavedForEachYear(Context context, Contact contact) {
        List<CalendarEvent> eventsSaved = new ArrayList<>();

        CalendarEvent eventToUpdate = getNextEventFromContact(context, contact);

        // Update events for each year
        EventWithoutYear eventWithoutYear = new EventWithoutYear(eventToUpdate);
        List<CalendarEvent> eventsAroundNeeded = eventWithoutYear.getEventsAroundAndForThisYear();
        List<CalendarEvent> eventsAroundSaved = getEventsFromContactWithYears(
                context, contact, eventWithoutYear.getListOfYearsForEachEvent());

        for (CalendarEvent event : eventsAroundNeeded) {
            if (eventsAroundSaved.contains(event)) {
                // For get id
                event = eventsAroundSaved.get(eventsAroundSaved.indexOf(event));
                eventsSaved.add(event);
            }
        }
        return eventsSaved;
    }

    public synchronized static List<CalendarEvent> getEventsSavedOrCreateNewsForEachYearAfterNextEvent(Context context, Contact contact) {
        List<CalendarEvent> eventsSaved = new ArrayList<>();

        CalendarEvent eventToUpdate = getNextEventOrCreateNewFromContact(context, contact);

        // Update events for each year
        EventWithoutYear eventWithoutYear = new EventWithoutYear(eventToUpdate);
        List<CalendarEvent> eventsAfterNeeded = eventWithoutYear.getEventsAfterThisYear();
        List<CalendarEvent> eventsAfterSaved = getEventsFromContactWithYears(
                context, contact, eventWithoutYear.getListOfYearsForEventsAfterThisYear());

        for (CalendarEvent event : eventsAfterNeeded) {
            if (eventsAfterSaved.contains(event)) {
                // For get id
                event = eventsAfterSaved.get(eventsAfterSaved.indexOf(event));
                eventsSaved.add(event);
            }
        }
        return eventsSaved;
    }

    /**
     * Return each event from contact
     * @param context Context to call
     * @param contact Contact associated with events
     * @param years List of event's years
     * @return Events for each year
     */
    public synchronized static List<CalendarEvent> getEventsFromContactWithYears(Context context, Contact contact, List<Integer> years) {
        Long[] eventTimes = new Long[years.size()];
        for(int i = 0; i < years.size(); i++) {
            int year = years.get(i);
            long eventTime = new DateTime(contact.getBirthday().getDateWithYear(year))
                    .withZoneRetainFields(DateTimeZone.UTC)
                    .toDateTime().toDate().getTime();
            eventTimes[i] = eventTime;
        }
        return getEventsFromContact(context, contact, eventTimes);
    }

    /**
     * Return new event from contact or null if not found
     * @param context Context to call
     * @param contact Contact associated with event
     * @return Next event in the year or null if not fund
     */
    public synchronized static CalendarEvent getNextEventFromContact(Context context, Contact contact) {
        Long[] eventTimes = new Long[1];
        eventTimes[0] = new DateTime(contact.getNextBirthday())
                .withZoneRetainFields(DateTimeZone.UTC)
                .toDateTime().toDate().getTime();
        contact.getNextBirthday();
        List<CalendarEvent> calendarEvents = getEventsFromContact(context, contact, eventTimes);
        if(calendarEvents.isEmpty())
            return null;
        else
            return calendarEvents.get(0);
    }

    public synchronized static CalendarEvent getNextEventOrCreateNewFromContact(Context context, Contact contact) {
        CalendarEvent nextEvent = getNextEventFromContact(context, contact);
        // If next event do not exists, create all events missing (end of 5 years)
        if(nextEvent == null) {
            saveEventsIfNotExistsFromAllContactWithBirthday(context);
            nextEvent = getNextEventFromContact(context, contact);
        }
        return nextEvent;
    }

    private synchronized static List<CalendarEvent> getEventsFromContact(Context context, Contact contact, Long[] eventTimes) {
        /* Two ways
            - Get events days of anniversary and filter with name (use for the first time)
            - Create links Event-Contact in custom table (may have bugs if event remove manually from calendar)
        */
        List<CalendarEvent> calendarEvents = new ArrayList<>();

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
            String where = CalendarContract.Events.DTSTART + " IN " + String.valueOf(QueryTool.getString(eventTimes)) +
                    " AND " + CalendarContract.Events.TITLE + " LIKE ?";
            String[] whereParam = {"%" + contact.getName() + "%"};
            // TODO better retrieve

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(
                    CalendarLoader.getBirthdayAdapterUri(context, CalendarContract.Events.CONTENT_URI),
                    projection,
                    where,
                    whereParam,
                    null);
            if(cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    CalendarEvent calendarEvent;
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
                    calendarEvents.add(calendarEvent);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
        return calendarEvents;
    }

    /**
     * Save all events and default reminders from contacts with birthday
     * @param context Context to call
     */
    public synchronized static void saveEventsIfNotExistsFromAllContactWithBirthday(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null) {
            Log.e(TAG, "Unable to get content resolver!");
            return;
        }

        long calendarId = CalendarLoader.getCalendar(context);
        if (calendarId == -1) {
            Log.e("CalendarSyncAdapter", "Unable to create calendar");
            return;
        }

        // Sync flow:
        // 1. Clear events table for this account completely
        //CalendarLoader.cleanTables(context, calendarId);
        // 2. Get birthdays from contacts
        // 3. Create events and reminders for each birthday

        //List<ContactEventOperation> contactEventOperationList = new ArrayList<>();
        ArrayList<ContentProviderOperation> allOperationList = new ArrayList<>();

        // iterate through all Contact
        List<Contact> contactList = ContactLoader.getAllContacts(context);

        int backRef = 0;
        for (Contact contact : contactList) {

            // TODO Ids
            Log.d(TAG, "BackRef is " + backRef);

            // If next event in calendar is empty, add new event
            CalendarEvent eventFromContentProvider = getNextEventFromContact(context, contact);
            if (eventFromContentProvider == null) {
                CalendarEvent eventToAdd = CalendarEvent.buildDefaultEventFromContactToSave(context, contact);

                // TODO ENCAPSULATE
                EventWithoutYear eventWithoutYear = new EventWithoutYear(eventToAdd);
                List<CalendarEvent> eventsAroundNeeded = eventWithoutYear.getEventsAroundAndForThisYear();
                List<CalendarEvent> eventsAroundSaved = getEventsFromContactWithYears(
                        context, contact, eventWithoutYear.getListOfYearsForEachEvent());

                for (CalendarEvent event : eventsAroundNeeded) {
                    if (!eventsAroundSaved.contains(event)) {

                        // Add event operation in list of contact manager
                        allOperationList.add(
                                EventProvider.insert(context, calendarId, event, contact));

                        //TODO REMOVE REMINDERS BUG
                        /*
                         * Gets ContentProviderOperation to insert new reminder to the
                         * ContentProviderOperation with the given backRef. This is done using
                         * "withValueBackReference"
                         */
                        int noOfReminderOperations = 0;
                        for (Reminder reminder : eventToAdd.getReminders()) {
                            allOperationList.add(ReminderProvider.insert(context, reminder, backRef));
                            noOfReminderOperations += 1;
                        }
                        // back references for the next reminders, 1 is for the event
                        backRef += 1 + noOfReminderOperations;

                    }
                }
            }
        }

        /* Create events with reminders and linkEventContract
         * intermediate commit - otherwise the binder transaction fails on large
         * operationList
         * TODO for large list > 200, make multiple apply
         */
        try {
            Log.d(TAG, "Start applying the batch...");

            /*
             * Apply all Reminder Operations
             */
            ContentProviderResult[] contentProviderResults =
                    contentResolver.applyBatch(CalendarContract.AUTHORITY, allOperationList);
            for(ContentProviderResult contentProviderResult : contentProviderResults) {
                Log.d(TAG, "ReminderOperation apply : " + contentProviderResult.toString());
            }

            Log.d(TAG, "Applying the batch was successful!");
        } catch (RemoteException |OperationApplicationException e) {
            Log.e(TAG, "Applying batch error!", e);
        }
    }

    /**
     * Utility method for add values in Builder
     * @param builder ContentProviderOperation.Builder
     * @param event Event to add
     */
    static synchronized void assignValuesInBuilder(ContentProviderOperation.Builder builder, CalendarEvent event) {
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

    public synchronized static void updateEvent(Context context, Contact contact, DateUnknownYear newBirthday) {
        for (CalendarEvent event : getEventsSavedOrCreateNewsForEachYearAfterNextEvent(context, contact)) {
            // Construct each anniversary of new birthday
            int year = new DateTime(event.getDate()).getYear();
            Date newBirthdayDate = DateUnknownYear.getDateWithYear(newBirthday.getDate(), year);
            event.setDateStart(newBirthdayDate);
            event.setAllDay(true);
            Log.e(TAG, "Update event : " + event.toString());
            ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            ContentProviderOperation contentProviderOperation = EventProvider.update(event);
            operations.add(contentProviderOperation);
            try {
                ContentProviderResult[] contentProviderResults =
                        context.getContentResolver().applyBatch(CalendarContract.AUTHORITY, operations);
                for(ContentProviderResult contentProviderResult : contentProviderResults) {
                    Log.d(TAG, contentProviderResult.toString());
                    if (contentProviderResult.uri != null)
                        Log.d(TAG, contentProviderResult.uri.toString());
                }
            } catch (RemoteException|OperationApplicationException e) {
                Log.e(TAG, "Unable to update event : " + e.getMessage());
            }
        }
    }

    public synchronized static void deleteEventsFromContact(Context context, Contact contact) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        for (CalendarEvent event : getEventsSavedForEachYear(context, contact)) {
            operations.add(ReminderProvider.deleteAll(context, event.getId()));
            operations.add(EventProvider.delete(event));
        }
        try {
            ContentProviderResult[] contentProviderResults =
                    context.getContentResolver().applyBatch(CalendarContract.AUTHORITY, operations);
            for(ContentProviderResult contentProviderResult : contentProviderResults) {
                Log.d(TAG, contentProviderResult.toString());
                if (contentProviderResult.uri != null)
                    Log.d(TAG, contentProviderResult.uri.toString());
            }
        } catch (RemoteException |OperationApplicationException e) {
            Log.e(TAG, "Unable to delete event : " + e.getMessage());
        }
    }
}
