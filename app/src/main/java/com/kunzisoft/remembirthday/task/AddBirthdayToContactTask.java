package com.kunzisoft.remembirthday.task;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.kunzisoft.remembirthday.element.DateUnknownYear;

import java.util.ArrayList;

/**
 * AsyncTask who store the birthday in contact database for a specific contact
 */
public class AddBirthdayToContactTask extends AsyncTask<Void, Void, Exception> {

    private static final String TAG = "AddBirthdayToContactTsk";

    private long contactId;
    private DateUnknownYear birthday;
    private Activity context;

    private CallbackAddBirthdayToContact callbackAddBirthdayToContact;

    public AddBirthdayToContactTask(long contactId, DateUnknownYear birthday, Activity context) {
        this.contactId = contactId;
        this.birthday = birthday;
        this.context = context;
    }

    @Override
    protected Exception doInBackground(Void... params) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?", new String[]{String.valueOf(contactId)})
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                    .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, "26-05-2015")
                    .build());
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch(Exception e) {
            Log.e(TAG, e.getMessage()+" ");
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Exception exception) {
        if(callbackAddBirthdayToContact != null)
            callbackAddBirthdayToContact.afterAddBirthdayInDatabase(exception);
    }

    public CallbackAddBirthdayToContact getCallbackAddBirthdayToContact() {
        return callbackAddBirthdayToContact;
    }

    public void setCallbackAddBirthdayToContact(CallbackAddBirthdayToContact callbackAddBirthdayToContact) {
        this.callbackAddBirthdayToContact = callbackAddBirthdayToContact;
    }

    /**
     * Callback for do action after insert birthday of contact in database
     */
    public interface CallbackAddBirthdayToContact {
        void afterAddBirthdayInDatabase(Exception exception);
    }
}