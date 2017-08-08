package com.kunzisoft.remembirthday.provider;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.Loader;

import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 08/08/17.
 */

public class ContactPhoneNumberLoader extends AbstractLoader {

    private CallbackActionPhoneNumber callbackActionPhoneNumber;

    public ContactPhoneNumberLoader(Context context, Contact contact) {
        super(context);

        uri = ContactsContract.Data.CONTENT_URI;
        projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE};
        selection = ContactsContract.Data.MIMETYPE + " = ? AND "
                + ContactsContract.Data.LOOKUP_KEY + " = '" + contact.getLookUpKey() + "'";
        selectionArgs = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
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
        if(callbackActionPhoneNumber != null)
            callbackActionPhoneNumber.afterActionPhoneNumberInDatabase(phoneNumbers);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
