package com.kunzisoft.remembirthday.activity;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kunzisoft.remembirthday.element.CalendarEvent;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.provider.ActionBirthdayInDatabaseTask;
import com.kunzisoft.remembirthday.provider.CalendarProvider;
import com.kunzisoft.remembirthday.provider.EventProvider;
import com.kunzisoft.remembirthday.provider.UpdateBirthdayToContactTask;

import java.util.ArrayList;

/**
 * Abstract class to encapsulate the management of the birthday dialog.
 * @author joker on 06/07/17.
 */
public class AbstractBuddyActivity extends AppCompatActivity
        implements ActionBirthdayInDatabaseTask.CallbackActionBirthday, AnniversaryDialogOpen {

    private static final String TAG_SELECT_DIALOG = "TAG_SELECT_DIALOG";

    private final static String CONTACT_KEY = "CONTACT_KEY";

    protected Contact contactSelected;
    protected BirthdayDialogFragment dialogSelection;

    private OnClickDialogListener onClickDialogListener =
            new OnClickDialogListener(contactSelected);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CONTACT_KEY, contactSelected);
    }

    /**
     * Initialize Dialog for selection of date, must be called only if "contactSelected" is initialized
     */
    protected void initDialogSelection(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            contactSelected = savedInstanceState.getParcelable(CONTACT_KEY);
        }
        onClickDialogListener = new OnClickDialogListener(contactSelected);
        // Initialize dialog for birthday selection
        dialogSelection = (BirthdayDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_SELECT_DIALOG);
        if (dialogSelection == null)
            dialogSelection = new BirthdayDialogFragment();
        attachDialogListener(contactSelected);

    }

    /**
     * Assign new current contact selected for Activity
     * @param contactSelected New current contact
     */
    public void setContactSelected(Contact contactSelected) {
        this.contactSelected = contactSelected;
    }

    /**
     * Attach dialog listener with a new contact
     * @param contactSelected New contact
     */
    public void attachDialogListener(Contact contactSelected) {
        this.onClickDialogListener.setContact(contactSelected);
        this.dialogSelection.setOnClickListener(onClickDialogListener);
        if(contactSelected != null)
            this.dialogSelection.setDefaultBirthday(contactSelected.getBirthday());
    }

    @Override
    public void openAnniversaryDialogSelection(Contact contact) {
        try {
            dialogSelection.show(getSupportFragmentManager(), TAG_SELECT_DIALOG);
        } catch(NullPointerException e) {
            Log.e(this.getClass().getSimpleName(), "'dialogSelection' must be initialized with 'initDialogSelection'");
        }
    }

    @Override
    public void afterActionBirthdayInDatabase(
            DateUnknownYear birthday, ActionBirthdayInDatabaseTask.CallbackActionBirthday.Action action, Exception exception) {
        CallbackAction.showMessage(this, action, exception);
    }


    /**
     * Utility class for manage click on Anniversary Dialog Listener for each element
     */
    private class OnClickDialogListener implements BirthdayDialogFragment.OnClickBirthdayListener {

        private Contact contact;

        public OnClickDialogListener(Contact contact) {
            this.contact = contact;
        }

        @Override
        public void onClickPositiveButton(DateUnknownYear dateUnknownYear) {
            // Update current birthday in database
            UpdateBirthdayToContactTask updateBirthdayToContactTask =
                    new UpdateBirthdayToContactTask(
                            AbstractBuddyActivity.this,
                            contact.getDataAnniversaryId(),
                            contact.getBirthday(),
                            dateUnknownYear);
            updateBirthdayToContactTask.setCallbackActionBirthday(AbstractBuddyActivity.this);
            updateBirthdayToContactTask.execute();

            // Update event in calendar
            CalendarEvent event = EventProvider.getNextEventFromContact(AbstractBuddyActivity.this, contact);
            if(event == null) {
                long calendarId = CalendarProvider.getCalendar(AbstractBuddyActivity.this);
                if (calendarId != -1) {
                    EventProvider.insert(AbstractBuddyActivity.this,
                            calendarId,
                            // TODO get reminders from list
                            CalendarEvent.buildCalendarEventFromContact(
                                    AbstractBuddyActivity.this,
                                    contact),
                            contact);
                } else {
                    Log.e("CalendarSyncAdapter", "Unable to create calendar");
                }
            } else {
                event.setDateStart(DateUnknownYear.getNextAnniversary(dateUnknownYear));
                event.setAllDay(true);
                Log.e(getClass().getSimpleName(), event.toString());
                ArrayList<ContentProviderOperation> operations = new ArrayList<>();
                ContentProviderOperation contentProviderOperation = EventProvider.update(event);
                operations.add(contentProviderOperation);
                try {
                    ContentProviderResult[] contentProviderResults =
                            getContentResolver().applyBatch(CalendarContract.AUTHORITY, operations);
                    for(ContentProviderResult contentProviderResult : contentProviderResults) {
                        Log.d(getClass().getSimpleName(), contentProviderResult.toString());
                        if (contentProviderResult.uri != null)
                            Log.d(getClass().getSimpleName(), contentProviderResult.uri.toString());
                    }
                } catch (RemoteException|OperationApplicationException e) {
                    Log.e(this.getClass().getSimpleName(), "Unable to update event : " + e.getMessage());
                }
            }
        }

        @Override
        public void onClickNegativeButton(DateUnknownYear selectedDate) {
        }

        public void setContact(Contact contact) {
            this.contact = contact;
        }
    }
}
