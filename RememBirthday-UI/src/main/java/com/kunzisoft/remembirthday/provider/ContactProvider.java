package com.kunzisoft.remembirthday.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 28/07/17.
 */
// TODO
public class ContactProvider {

    private static final String TAG = "ContactProvider";

    /**
     * Get RawContactId from ContactId
     * @param context context to call
     * @param contactId Id key of ContractsContract.Contacts
     * @return Id of RawContact
     */
    public static long getRawContactId(Context context, long contactId) {
        long rawContactId = -1;
        Cursor cursor = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID},
                ContactsContract.RawContacts.CONTACT_ID + "=?",
                new String[]{String.valueOf(contactId)}, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                rawContactId = cursor.getLong(0);
            }
            cursor.close();
        }
        return rawContactId;
    }

    /**
     * Return Contact from URI
     * @param context Context to call
     * @param contactData Contact URI
     * @return Contact from content resolver or null if not fund
     */
    public static Contact getContactFromURI(Context context, Uri contactData) {
        Contact contact = null;
        Cursor cursor =  context.getContentResolver().query(contactData, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contact = new Contact(
                        cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)),
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY)),
                        cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID)),
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));
            }
            cursor.close();
        }
        return contact;
    }

    /**
     * Get List of contacts with birthday
     * @return Cursor over all contacts with events, where accounts are not blacklisted
     */
    public static List<Contact> getAllContacts(Context context) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.Contacts.Data._ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.TYPE
        };
        String selection =
                ContactsContract.Data.MIMETYPE + "= ? AND (" +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + //" OR " +
                        //ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        //ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY +
                        " ) ";
        String[] selectionArgs = new String[] {
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };

        Cursor cursor =  context.getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                null);

        List<Contact> contactList = new ArrayList<>();
        if(cursor != null) {
            // TODO get only first for each contact
            while (cursor.moveToNext()) {
                int eventLookupKeyColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.LOOKUP_KEY);
                int displayNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                int eventDateColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);

                Contact contact = new Contact(cursor.getString(displayNameColumn));
                contact.setBirthday(DateUnknownYear.stringToDate(cursor.getString(eventDateColumn)));
                contact.setLookUpKey(cursor.getString(eventLookupKeyColumn));
                contactList.add(contact);
            }
            cursor.close();
        }

        return contactList;
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
