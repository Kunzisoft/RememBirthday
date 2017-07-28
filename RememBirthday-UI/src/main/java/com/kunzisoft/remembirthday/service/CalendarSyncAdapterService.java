package com.kunzisoft.remembirthday.service;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
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
import com.kunzisoft.remembirthday.provider.CalendarProvider;
import com.kunzisoft.remembirthday.provider.ContactProvider;
import com.kunzisoft.remembirthday.provider.EventProvider;
import com.kunzisoft.remembirthday.provider.ReminderProvider;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class CalendarSyncAdapterService extends Service {

    private static final String TAG = "CalendarSyncService";

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
        CalendarProvider.cleanTables(context, calendarId);
        // 2. Get birthdays from contacts
        // 3. Create events and reminders for each birthday

        List<ContactEventOperation> contactEventOperationList = new ArrayList<>();
        ArrayList<ContentProviderOperation> reminderOperationList = new ArrayList<>();

        // iterate through all Contact
        List<Contact> contactList = ContactProvider.getAllContacts(context);

        int backRef = 0;
        for (Contact contact : contactList) {
            ContactEventOperation contactEventOperation = new ContactEventOperation(contact);

            for (CalendarEvent calendarEvent : contact.getBirthdayEvent().getEventsAroundThisYear()) {

                // TODO Ids
                Log.d(TAG, "BackRef is " + backRef);

                // Add event operation in list of contact manager
                contactEventOperation.addContentProviderOperation(
                        EventProvider.insert(context, calendarId, calendarEvent, contact));

                //TODO REMOVE REMINDERS BUG

                /*
                 * Gets ContentProviderOperation to insert new reminder to the
                 * ContentProviderOperation with the given backRef. This is done using
                 * "withValueBackReference"
                 */
                int noOfReminderOperations = 0;
                for(Reminder reminder : calendarEvent.getReminders()) {
                    reminderOperationList.add(ReminderProvider.insert(context, reminder, backRef));
                    noOfReminderOperations += 1;
                }
                // back references for the next reminders, 1 is for the event
                backRef += 1 + noOfReminderOperations;
            }

            contactEventOperationList.add(contactEventOperation);

        }

        /* Create events with reminders and linkEventContract
         * intermediate commit - otherwise the binder transaction fails on large
         * operationList
         * TODO for large list > 200, make multiple apply
         */
        try {
            Log.d(TAG, "Start applying the batch...");

            /*
             * Apply all Event Operations and do link with contact
             */
            for(ContactEventOperation contactEventOperation : contactEventOperationList) {
                ContentProviderResult[] contentProviderResults =
                        contentResolver.applyBatch(CalendarContract.AUTHORITY, contactEventOperation.getContentProviderOperations());

                for(ContentProviderResult contentProviderResult : contentProviderResults) {
                    Log.d(TAG, "EventOperation apply : " + contentProviderResult.toString());
                    long eventId = Long.parseLong(contentProviderResult.uri.getLastPathSegment());
                    //Log.d(TAG, contactEventOperation.getContact() + " : " + eventId);
                }
            }

            /*
             * Apply all Reminder Operations
             */
            ContentProviderResult[] contentProviderResults =
                    contentResolver.applyBatch(CalendarContract.AUTHORITY, reminderOperationList);
            for(ContentProviderResult contentProviderResult : contentProviderResults) {
                Log.d(TAG, "ReminderOperation apply : " + contentProviderResult.toString());
            }

            Log.d(TAG, "Applying the batch was successful!");
        } catch (Exception e) {
            Log.e(TAG, "Applying batch error!", e);
        }
    }

    /**
     * Utility class for link a contact to specific ContentProviderOperation of event
     */
    private static class ContactEventOperation {
        private Contact contact;
        private ArrayList<ContentProviderOperation>  contentProviderOperations;

        ContactEventOperation(Contact contact) {
            this.contact = contact;
            this.contentProviderOperations = new ArrayList<>();
        }

        public Contact getContact() {
            return contact;
        }

        void addContentProviderOperation(ContentProviderOperation contentProviderOperation) {
            contentProviderOperations.add(contentProviderOperation);
        }

        ArrayList<ContentProviderOperation> getContentProviderOperations() {
            return contentProviderOperations;
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
