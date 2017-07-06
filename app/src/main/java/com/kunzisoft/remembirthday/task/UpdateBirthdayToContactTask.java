package com.kunzisoft.remembirthday.task;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.provider.ContactsContract;
import android.util.Log;

import com.kunzisoft.remembirthday.element.DateUnknownYear;

import java.util.ArrayList;

/**
 * Created by joker on 24/04/17.
 */

public class UpdateBirthdayToContactTask extends ActionBirthdayInDatabaseTask{

    private long dataId;
    private DateUnknownYear newBirthday;

    public UpdateBirthdayToContactTask(Activity context, long dataId, DateUnknownYear oldBirthday, DateUnknownYear newBirthday) {
        super(context, oldBirthday);
        this.dataId = dataId;
        this.newBirthday = newBirthday;
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
                                    birthday.toBackupString(),
                                    String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)})
                    .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, newBirthday.toBackupString());
            ops.add(contentBuilder.build());
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch(Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage()+" ");
            return e;
        }
        return null;
    }
}
