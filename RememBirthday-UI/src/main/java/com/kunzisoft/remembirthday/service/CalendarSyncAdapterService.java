package com.kunzisoft.remembirthday.service;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.util.Log;

import com.kunzisoft.remembirthday.account.AccountResolver;
import com.kunzisoft.remembirthday.account.CalendarAccount;
import com.kunzisoft.remembirthday.element.CalendarEvent;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.Reminder;
import com.kunzisoft.remembirthday.preference.PreferencesManager;
import com.kunzisoft.remembirthday.provider.CalendarProvider;
import com.kunzisoft.remembirthday.provider.EventProvider;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressLint("NewApi")
public class CalendarSyncAdapterService extends Service {

    private static final String TAG = "CalendarSyncService";

    /**
     * Manage calendar between X years and Y Years of current year
     */
    private static final int X_YEAR = 3;
    private static final int Y_YEAR = 5;

    public CalendarSyncAdapterService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CalendarSyncAdapter().getSyncAdapterBinder();
    }

    private static void performSync(Context context, Account account, Bundle extras,
                                    String authority, ContentProviderClient provider, SyncResult syncResult)
            throws OperationCanceledException {
        performSync(context);
    }

    public static void performSync(Context context) {
        Log.d(TAG, "Starting sync...");

        ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null) {
            Log.e(TAG, "Unable to get content resolver!");
            return;
        }

        long calendarId = CalendarProvider.getCalendar(context);
        if (calendarId == -1) {
            Log.e("CalendarSyncAdapter", "Unable to create calendar");
            return;
        }

        // Sync flow:
        // 1. Clear events table for this account completely
        CalendarProvider.cleanTables(context, contentResolver, calendarId);
        // 2. Get birthdays from contacts
        // 3. Create events and reminders for each birthday

        // collection of birthdays that will later be added to the calendar
        ArrayList<ContentProviderOperation> operationList = new ArrayList<>();

        // iterate through all Contact Events
        List<Contact> contactList = EventProvider.getContacts(context, contentResolver);

        int backRef = 0;
        for (Contact contact : contactList) {

            /*
             * Insert events for the past 3 years and the next 5 years.
             */
            Calendar currCal = Calendar.getInstance();
            int currYear = currCal.get(Calendar.YEAR);
            int startYear = currYear - X_YEAR;
            int endYear = currYear + Y_YEAR;

            for (int iteratedYear = startYear; iteratedYear <= endYear; iteratedYear++) {
                Log.d(TAG, "iteratedYear: " + iteratedYear);

                // calculate age
                int age = iteratedYear - new DateTime(contact.getBirthday().getDate()).getYear();
                CalendarEvent calendarEvent = new CalendarEvent(contact.getBirthday().getDate());
                calendarEvent.setYear(iteratedYear);
                calendarEvent.setAllDay(true);
                calendarEvent.setTitle(contact.getName() + " " + age);
                // Assign new reminder with default values
                int[] defaultTime = PreferencesManager.getDefaultTime(context);
                for(int dayBefore : PreferencesManager.getDefaultDays(context))
                calendarEvent.addReminder(new Reminder(calendarEvent.getDate(),
                        defaultTime[0], defaultTime[1],
                        dayBefore));
                Log.d(TAG, "Add event : " + calendarEvent);
                // TODO Ids
                Log.d(TAG, "BackRef is " + backRef);
                operationList.add(EventProvider.insert(context, calendarId, calendarEvent, contact));
                /*
                 * Gets ContentProviderOperation to insert new reminder to the
                 * ContentProviderOperation with the given backRef. This is done using
                 * "withValueBackReference"
                 */
                int noOfReminderOperations = 0;
                for(Reminder reminder : calendarEvent.getReminders()) {

                    ContentProviderOperation.Builder builder = ContentProviderOperation
                            .newInsert(CalendarProvider.getBirthdayAdapterUri(context, CalendarContract.Reminders.CONTENT_URI));

                    /*
                     * add reminder to last added event identified by backRef
                     *
                     * see http://stackoverflow.com/questions/4655291/semantics-of-
                     * withvaluebackreference
                     */
                    builder.withValueBackReference(CalendarContract.Reminders.EVENT_ID, backRef);
                    builder.withValue(CalendarContract.Reminders.MINUTES, reminder.getMinutesBeforeEvent());
                    builder.withValue(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                    operationList.add(builder.build());
                    Log.d(TAG, "Add reminder : " + reminder);

                    noOfReminderOperations += 1;
                }

                // back references for the next reminders, 1 is for the event
                backRef += 1 + noOfReminderOperations;

                /*
                 * intermediate commit - otherwise the binder transaction fails on large
                 * operationList
                 */
                applyBatchByOperationListSize(operationList, contentResolver, 200);
            }
        }

        /* Create events */
        applyBatchByOperationListSize(operationList, contentResolver, 0);
    }

    /**
     * Apply batch if operationList is larger than "listSize"
     * @param operationList List of Operations
     * @param contentResolver Content Resolver
     * @param listSize Size minimal
     */
    private static void applyBatchByOperationListSize(ArrayList<ContentProviderOperation> operationList,
                                               ContentResolver contentResolver,
                                               int listSize) {
        if (operationList.size() > listSize) {
            try {
                Log.d(TAG, "Start applying the batch...");
                contentResolver.applyBatch(CalendarContract.AUTHORITY, operationList);
                Log.d(TAG, "Applying the batch was successful!");
            } catch (Exception e) {
                Log.e(TAG, "Applying batch error!", e);
            }
        }
    }


    private class CalendarSyncAdapter extends AbstractThreadedSyncAdapter {

        CalendarSyncAdapter() {
            super(CalendarSyncAdapterService.this, true);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority,
                                  ContentProviderClient provider, SyncResult syncResult) {
            try {
                CalendarSyncAdapterService.performSync(CalendarSyncAdapterService.this, account, extras, authority,
                        provider, syncResult);
            } catch (OperationCanceledException e) {
                Log.e(getClass().getSimpleName(), "OperationCanceledException", e);
            }
        }

        @Override
        public void onSecurityException(Account account, Bundle extras, String authority, SyncResult syncResult) {
            super.onSecurityException(account, extras, authority, syncResult);

            // contact or calendar permission has been revoked -> simply remove account
            AccountResolver accountResolver = CalendarAccount.getAccount(CalendarSyncAdapterService.this, null);
            accountResolver.removeAccount();
        }
    }
}
