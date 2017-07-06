package com.kunzisoft.remembirthday.database;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.kunzisoft.remembirthday.element.Contact;

/**
 * Created by joker on 06/07/17.
 */

public class ContactBuild {

    public static long getRawContactId(Context context, long contactId) {
        long rawContactId = -1;
        Cursor c = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID},
                ContactsContract.RawContacts.CONTACT_ID + "=?",
                new String[]{String.valueOf(contactId)}, null);
        try {
            if (c.moveToFirst()) {
                rawContactId = c.getLong(0);
            }
        } finally {
            c.close();
        }
        return rawContactId;
    }

    /**
     * Assign RAW_CONTACT_ID to contact in parameter with CONTACT_ID,
     * If CONTACT_ID is undefined, RAW_CONTACT_ID is set to undefined
     * @param context Context to call
     * @param contact Contact to modify
     * @return first RAW_CONTACT_ID assign
     */
    public static long assignRawContactIdToContact(Context context, Contact contact) {
        long rawId = Contact.ID_UNDEFINED;
        if(contact.getId() != Contact.ID_UNDEFINED)
            rawId = getRawContactId(context, contact.getId());
        contact.setRawId(rawId);
        return rawId;
    }
}
