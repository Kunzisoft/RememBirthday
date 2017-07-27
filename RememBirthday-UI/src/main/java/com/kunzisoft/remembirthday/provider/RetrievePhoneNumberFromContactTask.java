package com.kunzisoft.remembirthday.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.kunzisoft.remembirthday.element.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 15/07/17.
 */
public class RetrievePhoneNumberFromContactTask extends AsyncTask<Void, Void, List<PhoneNumber>> {

    private ContentResolver contentResolver;
    private long contactId;
    private String lookupKey;
    private CallbackActionPhoneNumber callbackActionPhoneNumber;
    private String[] projection = new String[] {
            ContactsContract.Contacts._ID,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.LOOKUP_KEY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE
    };

    private String[] selectionArgs = new String[] {
        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
    };

    public RetrievePhoneNumberFromContactTask(Context context, long contactId, String lookupKey) {
        this.contentResolver = context.getContentResolver();
        this.contactId = contactId;
        this.lookupKey = lookupKey;
    }

    @Override
    protected List<PhoneNumber> doInBackground(Void... voids) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        Cursor cursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                ContactsContract.Data.MIMETYPE + " = ? AND "
                + ContactsContract.Data.LOOKUP_KEY + " = '" + lookupKey + "'",
                selectionArgs,
                null);
        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                /*
                long contactId = cursor.getLong(cursor.getColumnIndex(
                        ContactsContract.Data._ID));
                String lookupkey = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Data.LOOKUP_KEY));
                        */
                String number = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.TYPE));
                phoneNumbers.add(new PhoneNumber(number, type));
                cursor.moveToNext();
            }
            // TODO try catch with id
            cursor.close();
        }
        return phoneNumbers;
    }

    @Override
    protected void onPostExecute(List<PhoneNumber> phoneNumberList) {
        if(callbackActionPhoneNumber != null)
            callbackActionPhoneNumber.afterActionPhoneNumberInDatabase(phoneNumberList);
    }

    public void setCallbackActionPhoneNumber(CallbackActionPhoneNumber callbackActionPhoneNumber) {
        this.callbackActionPhoneNumber = callbackActionPhoneNumber;
    }

    /**
     * Callback for do action after get phone numbers of contact in database
     */
    public interface CallbackActionPhoneNumber {
        void afterActionPhoneNumberInDatabase(List<PhoneNumber> phoneNumberList);
    }
}
