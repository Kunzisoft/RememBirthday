package com.kunzisoft.remembirthday.activity;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.CalendarEvent;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.element.Reminder;
import com.kunzisoft.remembirthday.preference.PreferencesManager;
import com.kunzisoft.remembirthday.provider.ActionBirthdayInDatabaseTask;
import com.kunzisoft.remembirthday.provider.CalendarProvider;
import com.kunzisoft.remembirthday.provider.ContactProvider;
import com.kunzisoft.remembirthday.provider.EventProvider;
import com.kunzisoft.remembirthday.provider.ReminderProvider;

import java.util.ArrayList;

/**
 * Created by joker on 19/01/17.
 */
public class ListContactsActivity extends AppCompatActivity
        implements ActionBirthdayInDatabaseTask.CallbackActionBirthday, AnniversaryDialogOpen {

    public static final int INSERT_BIRTHDAY_RESULT_CODE = 1619;
    private static final int INSERT_CONTACT_RESULT_CODE = 1567;

    // Dialog for birthday selection
    private static final String TAG_SELECT_DIALOG = "TAG_SELECT_DIALOG";
    private BirthdayDialogFragment dialogSelection;
    private Contact contactWithRawIdSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.select_contact_title));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton addContactButton = (FloatingActionButton) findViewById(R.id.fab_add_buddy);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, INSERT_CONTACT_RESULT_CODE);
            }
        });

        // Initialize dialog for birthday selection
        dialogSelection = (BirthdayDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_SELECT_DIALOG);
        if(dialogSelection == null)
            dialogSelection = new BirthdayDialogFragment();

        dialogSelection.setOnClickListener(new BirthdayDialogFragment.OnClickBirthdayListener() {
            @Override
            public void onClickPositiveButton(DateUnknownYear dateUnknownYear) {
                contactWithRawIdSelected.setBirthday(dateUnknownYear);

                // All operations to apply
                ArrayList<ContentProviderOperation> allOperationList = new ArrayList<>();

                // Add new birthday in database
                ActionBirthdayInDatabaseTask.AddBirthdayToContactTask addBirthdayToContactTask =
                        new ActionBirthdayInDatabaseTask.AddBirthdayToContactTask(
                                ListContactsActivity.this,
                                contactWithRawIdSelected.getRawId(),
                                dateUnknownYear);
                addBirthdayToContactTask.setCallbackActionBirthday(ListContactsActivity.this);
                addBirthdayToContactTask.execute();
                setResult(Activity.RESULT_OK);
                finish();

                // Add new event in calendar
                if(PreferencesManager.isCustomCalendarActive(ListContactsActivity.this)) {
                    // TODO Encapsulate
                    long calendarId = CalendarProvider.getCalendar(ListContactsActivity.this);
                    if (calendarId != -1) {
                        CalendarEvent event = CalendarEvent.buildDefaultEventFromContactToSave(
                                ListContactsActivity.this,
                                contactWithRawIdSelected);
                        allOperationList.add(
                                EventProvider.insert(ListContactsActivity.this,
                                        calendarId,
                                        event,
                                        contactWithRawIdSelected));
                        for (Reminder reminder : event.getReminders()) {
                            allOperationList.add(
                                    ReminderProvider.insert(
                                            ListContactsActivity.this,
                                            reminder,
                                            0));
                        }
                    } else {
                        Log.e("CalendarSyncAdapter", "Unable to create calendar");
                    }
                    try {
                        getContentResolver().applyBatch(CalendarContract.AUTHORITY, allOperationList);
                    } catch (RemoteException | OperationApplicationException e) {
                        Log.e(getClass().getSimpleName(), "Applying batch error!", e);
                    }
                }
            }

            @Override
            public void onClickNegativeButton(DateUnknownYear selectedDate) {}
        });
    }

    @Override
    public void openAnniversaryDialogSelection(Contact contact) {
        this.contactWithRawIdSelected = contact;
        dialogSelection.show(getSupportFragmentManager(), TAG_SELECT_DIALOG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (INSERT_CONTACT_RESULT_CODE) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    contactWithRawIdSelected = ContactProvider.getContactFromURI(this, contactData);
                }
                break;
        }
    }

    @Override
    public void afterActionBirthdayInDatabase(DateUnknownYear birthday, Action action, Exception exception) {
        CallbackAction.showMessage(this, action, exception);
    }
}
