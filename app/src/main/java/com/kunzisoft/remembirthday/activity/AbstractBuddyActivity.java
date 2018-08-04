package com.kunzisoft.remembirthday.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kunzisoft.autosms.database.AutoSmsDbHelper;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.preference.PreferencesHelper;
import com.kunzisoft.remembirthday.provider.ContactProvider;
import com.kunzisoft.remembirthday.provider.EventLoader;

/**
 * Abstract class to encapsulate the management of the birthday dialog.
 * @author joker on 06/07/17.
 */
public class AbstractBuddyActivity extends AppCompatActivity
        implements ContactProvider.CallbackActionBirthday, AnniversaryDialogOpen {

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
        if(contactSelected != null && contactSelected.hasBirthday())
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
            DateUnknownYear birthday, ContactProvider.CallbackActionBirthday.Action action, Exception exception) {
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
        public void onClickPositiveButton(DateUnknownYear newBirthday) {
            // Update current birthday in database
            ContactProvider.UpdateBirthdayContactProvider updateBirthdayContactProvider =
                    new ContactProvider.UpdateBirthdayContactProvider(
                            AbstractBuddyActivity.this,
                            contact.getDataAnniversaryId(),
                            contact.getBirthday(),
                            newBirthday);
            updateBirthdayContactProvider.setCallbackActionBirthday(AbstractBuddyActivity.this);
            updateBirthdayContactProvider.execute();

            // Update event in calendar
            if(PreferencesHelper.isCustomCalendarActive(AbstractBuddyActivity.this)) {
                try {
                    EventLoader.updateEvent(AbstractBuddyActivity.this, contact, newBirthday);
                } catch (EventLoader.EventException e) {
                    Log.e(getClass().getSimpleName(), "Error when updating event : " + e.getLocalizedMessage());
                }
            }

            // Delete auto-message // TODO update
            AutoSmsDbHelper.getDbHelper(AbstractBuddyActivity.this).deleteAllByLookupKey(contact.getLookUpKey());
        }

        @Override
        public void onClickNegativeButton(DateUnknownYear selectedDate) {
        }

        public void setContact(Contact contact) {
            this.contact = contact;
        }
    }
}
