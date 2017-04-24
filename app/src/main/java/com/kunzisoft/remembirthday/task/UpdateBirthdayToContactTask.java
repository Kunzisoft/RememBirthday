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

    private DateUnknownYear newBirthday;

    public UpdateBirthdayToContactTask(long contactId, DateUnknownYear oldBirthday, DateUnknownYear newBirthday, Activity context) {
        super(contactId, oldBirthday, context);
        this.newBirthday = newBirthday;
    }

    @Override
    protected Exception doInBackground(Void... voids) {
        try {
            String typeString;
            if(birthday.containsYear())
                typeString = String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
            else
                typeString = String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY);

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ContentProviderOperation.Builder contentBuilder =  ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data._ID + " =? AND " +
                                    ContactsContract.Data.MIMETYPE + " =? AND " +
                                    ContactsContract.CommonDataKinds.Event.START_DATE + " =? AND " +
                                    ContactsContract.CommonDataKinds.Event.TYPE + " =?"
                            , new String[]{String.valueOf(contactId),
                                    ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                                    birthday.toString(),
                                    typeString})
                    .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, newBirthday.toString());
            ops.add(contentBuilder.build());
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch(Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage()+" ");
            return e;
        }
        return null;
    }
}
