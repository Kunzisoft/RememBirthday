package com.kunzisoft.remembirthday.task;

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

    public RemoveBirthdayFromContactTask(long contactId, DateUnknownYear birthday, Activity context) {
        super(contactId, birthday, context);
    }

    @Override
    protected Exception doInBackground(Void... params) {
        try {
            String typeString;
            if(birthday.containsYear())
                typeString = String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
            else
                typeString = String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY);

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ContentProviderOperation.Builder contentBuilder =  ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                    .withSelection(
                            ContactsContract.Data._ID + " =? AND " +
                            ContactsContract.Data.MIMETYPE + " =? AND " +
                            ContactsContract.CommonDataKinds.Event.START_DATE + " =? AND " +
                            ContactsContract.CommonDataKinds.Event.TYPE + " =?"
                            , new String[]{String.valueOf(contactId),
                                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                                birthday.toString(),
                                typeString});
            ops.add(contentBuilder.build());
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch(Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage()+" ");
            return e;
        }
        return null;
    }
}