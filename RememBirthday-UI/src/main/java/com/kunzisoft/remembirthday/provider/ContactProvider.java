package com.kunzisoft.remembirthday.provider;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.kunzisoft.remembirthday.element.DateUnknownYear;

import java.util.ArrayList;

/**
 * Created by joker on 19/04/17.
 */
// TODO ContactAsyncQueryHandler
public abstract class ContactProvider extends AsyncTask<Void, Void, Exception> {

    protected DateUnknownYear birthday;
    protected Activity context;

    protected CallbackActionBirthday callbackActionBirthday;
    protected CallbackActionBirthday.Action action;

    public ContactProvider(Activity context, DateUnknownYear birthday) {
        this.birthday = birthday;
        this.context = context;
        this.action = CallbackActionBirthday.Action.UNDEFINED;
    }

    @Override
    protected void onPostExecute(Exception exception) {
        if(exception != null)
            Log.e(getClass().getSimpleName(), "Error in contact provider : " + exception.getMessage());
        if(callbackActionBirthday != null)
            callbackActionBirthday.afterActionBirthdayInDatabase(birthday, action, exception);
    }

    public CallbackActionBirthday getCallbackActionBirthday() {
        return callbackActionBirthday;
    }

    public void setCallbackActionBirthday(CallbackActionBirthday callbackActionBirthday) {
        this.callbackActionBirthday = callbackActionBirthday;
    }

    /**
     * Callback for do action after insert/update/delete birthday of contact in database
     */
    public interface CallbackActionBirthday {

        enum Action {
            UNDEFINED,
            ADD,
            UPDATE,
            REMOVE
        }

        void afterActionBirthdayInDatabase(DateUnknownYear birthday, Action action, Exception exception);
    }

    /**
     * AsyncTask who store the birthday in database for a specific contact
     */
    public static class AddBirthdayContactProvider extends ContactProvider {

        private long rawContactId;

        public AddBirthdayContactProvider(Activity context, long rawContactId, DateUnknownYear birthday) {
            super(context, birthday);
            this.rawContactId = rawContactId;
            this.action = CallbackActionBirthday.Action.ADD;
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ContentProviderOperation.Builder contentBuilder =  ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, birthday.toBackupString())
                        .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
                Log.d(getClass().getSimpleName(), "Add birthday " + birthday);
                ops.add(contentBuilder.build());
                ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                if(results[0] == null)
                    throw new Exception("Unable to add new birthday");
            } catch(Exception e) {
                return e;
            }
            return null;
        }
    }

    /**
     * AsyncTask who store the birthday in database for a specific contact
     */
    public static class RemoveBirthdayContactProvider extends ContactProvider {

        protected long dataId;

        public RemoveBirthdayContactProvider(Activity context, long dataId, DateUnknownYear birthday) {
            super(context, birthday);
            this.dataId = dataId;
            this.action = CallbackActionBirthday.Action.REMOVE;
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ContentProviderOperation.Builder contentBuilder =  ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(
                                ContactsContract.Data._ID + " =? AND " +
                                ContactsContract.Data.MIMETYPE + " =? AND " +
                                ContactsContract.CommonDataKinds.Event.START_DATE + " =? AND " +
                                ContactsContract.CommonDataKinds.Event.TYPE + " =?"
                                , new String[]{String.valueOf(dataId),
                                    ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                                    birthday.toBackupString(),
                                    String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)});
                Log.d(getClass().getSimpleName(), "Delete birthday " + birthday);
                ops.add(contentBuilder.build());
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch(Exception e) {
                return e;
            }
            return null;
        }
    }

    /**
     * Created by joker on 24/04/17.
     */
    public static class UpdateBirthdayContactProvider extends ContactProvider {

        private long dataId;
        private DateUnknownYear oldBirthday;

        public UpdateBirthdayContactProvider(Activity context, long dataId, DateUnknownYear oldBirthday, DateUnknownYear newBirthday) {
            super(context, newBirthday);
            this.dataId = dataId;
            this.oldBirthday = oldBirthday;
            this.action = CallbackActionBirthday.Action.UPDATE;
        }

        @Override
        protected Exception doInBackground(Void... voids) {
            try {
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ContentProviderOperation.Builder contentBuilder =  ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + " =? AND " +
                                        ContactsContract.Data.MIMETYPE + " =? AND " +
                                        ContactsContract.CommonDataKinds.Event.START_DATE + " =? AND " +
                                        ContactsContract.CommonDataKinds.Event.TYPE + " =?"
                                , new String[]{String.valueOf(dataId),
                                        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                                        oldBirthday.toBackupString(),
                                        String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)})
                        .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, birthday.toBackupString());
                Log.d(getClass().getSimpleName(), "Update birthday " + oldBirthday + " to " + birthday);
                ops.add(contentBuilder.build());
                ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                if(results[0].count == 0)
                    return new Exception("Unable to update birthday");
            } catch(Exception e) {
                return e;
            }
            return null;
        }
    }
}
