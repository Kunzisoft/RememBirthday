package com.kunzisoft.remembirthday.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.kunzisoft.remembirthday.factory.ContactSort;
import com.kunzisoft.remembirthday.preference.PreferencesManager;

/**
 * Created by joker on 05/08/17.
 */

public abstract class ContactLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;

    // Connexion to content provider
    protected Uri uri;
    protected String[] projection;
    protected String selection;
    protected String[] selectionArgs;
    protected String sortOrder;

    protected ContactSort contactSort;

    private LoaderContactCallbacks loaderContactCallback;

    public ContactLoader(Context context) {
        this.context = context;
    }

    public void setLoaderContactCallback(LoaderContactCallbacks loaderContactCallback) {
        this.loaderContactCallback = loaderContactCallback;
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
     * Get sort of contact (combined ASC, DESC and list sort)
     * @return ContactSort
     */
    public ContactSort getContactSort() {
        return contactSort;
    }

    /**
     * Loader who get all contacts
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
     * Loader who get contacts with birthdays
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

            contactSort = PreferencesManager.getDefaultContactSort(context);
        }
    }

    /**
     * Interface for callback methods of LoaderContact
     */
    public interface LoaderContactCallbacks {
        void onContactLoadFinished(Loader<android.database.Cursor> loader, android.database.Cursor cursor);
        void onContactLoaderReset(Loader<Cursor> loader);
    }
}
