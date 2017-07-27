package com.kunzisoft.remembirthday.provider;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.provider.ContactsContract;
import android.util.Log;

import com.kunzisoft.remembirthday.element.DateUnknownYear;

import java.util.ArrayList;

/**
 * AsyncTask who store the birthday in database for a specific contact
 */
public class RemoveBirthdayFromContactTask extends ActionBirthdayInDatabaseTask {

    protected long dataId;

    public RemoveBirthdayFromContactTask(Activity context, long dataId, DateUnknownYear birthday) {
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
            ops.add(contentBuilder.build());
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch(Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage()+" ");
            return e;
        }
        return null;
    }
}