package com.kunzisoft.remembirthday.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.factory.ContactSort;
import com.kunzisoft.remembirthday.preference.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 05/08/17.
 */

public abstract class ContactLoader extends AbstractLoader {

    private static final String TAG = "ContactLoader";

    protected ContactSort contactSort;
    private LoaderContactCallbacks loaderContactCallback;

    public ContactLoader(Context context) {
        super(context);
    }

    /**
     * Get RawContactId from ContactId
     * @param context context to call
     * @param contactId Id key of ContractsContract.Contacts
     * @return Id of RawContact
     */
    public static long getRawContactId(Context context, long contactId) {
        long rawContactId = -1;
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
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
     * Get List of contacts with birthdays in callback when loading is finished
     * @param context Context to call
     * @param loaderManager Loader Manager to init loader
     * @param contactsCallbacks Callback to retrieve contacts
     */
    public static void getAllContacts(final Context context, LoaderManager loaderManager,
                                               final ContactsCallbacks contactsCallbacks) {
        ContactBirthdayLoader contactBirthdayLoader = new ContactBirthdayLoader(context);
        contactBirthdayLoader.setLoaderContactCallback(new LoaderContactCallbacks() {
            @Override
            public void onContactLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                contactsCallbacks.onContactsLoadFinished(getContactsFromCursor(cursor));
            }

            @Override
            public void onContactLoaderReset(Loader<Cursor> loader) {}
        });
        loaderManager.initLoader(0, null, contactBirthdayLoader);
    }

    public static List<Contact> getAllContacts(Context context) {
        ContactBirthdayLoader contactBirthdayLoader = new ContactBirthdayLoader(context);
        Cursor cursor = context.getContentResolver().query(
                contactBirthdayLoader.uri,
                contactBirthdayLoader.projection,
                contactBirthdayLoader.selection,
                contactBirthdayLoader.selectionArgs,
                contactBirthdayLoader.sortOrder);
        return getContactsFromCursor(cursor);
    }

    public static List<Contact> getContactsFromCursor(Cursor cursor) {
        List<Contact> contactList = new ArrayList<>();
        if(cursor != null) {
            // TODO getAutoSmsById only first for each contact
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(contactSort != null && contactSort.getOrderByQuery() != null) {
            sortOrder = contactSort.getOrderByQuery();
        }
        // Starts the query
        return new CursorLoader(
                context,
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(loaderContactCallback != null)
            loaderContactCallback.onContactLoadFinished(loader, cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loaderContactCallback != null)
            loaderContactCallback.onContactLoaderReset(loader);
    }

    /**
     * Assign listener for callback
     * @param loaderContactCallback Contact callback
     */
    public void setLoaderContactCallback(LoaderContactCallbacks loaderContactCallback) {
        this.loaderContactCallback = loaderContactCallback;
    }

    /**
     * Get sort of contact (combined ASC, DESC and list sort)
     * @return ContactSort
     */
    public ContactSort getContactSort() {
        return contactSort;
    }

    /**
     * Loader who getAutoSmsById all contacts
     */
    public static class ContactBaseLoader extends ContactLoader {

        public ContactBaseLoader(Context context) {
            super(context);

            uri = ContactsContract.Contacts.CONTENT_URI;
            projection = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                    ContactsContract.Contacts.PHOTO_URI
            };
            selection = null;
            selectionArgs = null;
            sortOrder = null;

            contactSort = ContactSort.CONTACT_SORT_BY_NAME;
        }
    }

    /**
     * Loader who getAutoSmsById contacts with birthdays
     */
    public static class ContactBirthdayLoader extends ContactLoader {

        public ContactBirthdayLoader(Context context) {
            super(context);

            uri = ContactsContract.Data.CONTENT_URI;
            projection = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                    ContactsContract.Contacts.PHOTO_URI,
                    ContactsContract.Contacts.Data._ID,
                    ContactsContract.CommonDataKinds.Event.START_DATE,
                    ContactsContract.CommonDataKinds.Event.TYPE
            };
            selection =
                    ContactsContract.Data.MIMETYPE + "= ? AND (" +
                            ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY +
                            " ) ";
            selectionArgs = new String[] {
                    ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
            };

            contactSort = PreferencesHelper.getDefaultContactSort(context);
        }
    }

    /**
     * Interface for callback methods of LoaderContact
     */
    public interface LoaderContactCallbacks {
        void onContactLoadFinished(Loader<android.database.Cursor> loader, android.database.Cursor cursor);
        void onContactLoaderReset(Loader<Cursor> loader);
    }

    /**
     * Interface for callback methods of LoaderContact
     */
    public interface ContactsCallbacks {
        void onContactsLoadFinished(List<Contact> contacts);
    }
}
