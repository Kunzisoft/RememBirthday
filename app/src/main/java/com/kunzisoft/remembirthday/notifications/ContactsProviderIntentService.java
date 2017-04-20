package com.kunzisoft.remembirthday.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;

/**
 * Created by joker on 20/04/17.
 */

public class ContactsProviderIntentService extends IntentService implements Loader.OnLoadCompleteListener<Cursor> {

    // Redefined Query
    private Uri contentUri = ContactsContract.Data.CONTENT_URI;
    private String[] projection = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.CommonDataKinds.Event.START_DATE,
            ContactsContract.CommonDataKinds.Event.TYPE
    };
    private String selection =
            ContactsContract.Data.MIMETYPE + "= ? AND (" +
                    ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                    ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + " OR " +
                    ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                    ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY +
                    " ) ";
    private String[] selectionArgs = new String[] {
            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
    };
    protected String orderBy = null;
    // Cursor and id
    private static final int LOADER_ID_NETWORK = 1549;
    private CursorLoader mCursorLoader;

    public ContactsProviderIntentService() {
        super(ContactsProviderIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mCursorLoader = new CursorLoader(
                this,
                contentUri,
                projection,
                selection,
                selectionArgs,
                orderBy);
        mCursorLoader.registerListener(LOADER_ID_NETWORK, this);
        mCursorLoader.startLoading();
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Contact contact = new Contact(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
            DateUnknownYear dateUnknownYear = null;
            try {
                // TODO Change anniversary
                switch(cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE))) {
                    case ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY:
                        dateUnknownYear = DateUnknownYear.stringToDateWithKnownYear(
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE)));
                        break;
                    case ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY:
                        dateUnknownYear = DateUnknownYear.stringToDateWithUnknownYear(
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE)));
                        break;
                }
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Birthday can't be extract : " + e.getMessage());
            } finally {
                if(dateUnknownYear != null)
                    contact.setBirthday(dateUnknownYear);
            }

            Log.d(getClass().getSimpleName(), contact.toString());

            // Create alarm for birthday of this contact
            // TODO define anniversary
            if(contact.hasBirthday())
                NotificationEventReceiver.setupAlarm(this, contact);

            // Go to next contact
            cursor.moveToNext();
            //TODO photo
        }
        cursor.close();

        // Stop the cursor loader
        if (mCursorLoader != null) {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
