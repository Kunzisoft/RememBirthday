package com.kunzisoft.remembirthday.task;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.provider.ContactsContract;
import android.util.Log;

import com.kunzisoft.remembirthday.element.DateUnknownYear;

import java.util.ArrayList;

/**
 * AsyncTask who store the birthday in database for a specific contact
 */
public class AddBirthdayToContactTask extends ActionBirthdayInDatabaseTask {

    private long rawContactId;

    public AddBirthdayToContactTask(Activity context, long rawContactId, DateUnknownYear birthday) {
        super(context, birthday);
        this.rawContactId = rawContactId;
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
            ops.add(contentBuilder.build());
            ContentProviderResult[] contentProviderResults = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            int dataId = Integer.parseInt(contentProviderResults[0].uri.getLastPathSegment());

        } catch(Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage()+" ");
            return e;
        }
        return null;
    }
}