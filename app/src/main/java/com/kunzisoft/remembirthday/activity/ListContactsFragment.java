package com.kunzisoft.remembirthday.activity;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.task.AddBirthdayToContactTask;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Fragment that retrieves and displays the list of contacts
 */
public class ListContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener, AddBirthdayToContactTask.CallbackAddBirthdayToContact {

    private static final String TAG = "ListContactsFragment";

    /*
    CURSOR ADAPTER
     */
    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
    };

    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            R.id.item_list_contact_name,
            R.id.item_list_contact_icon
    };

    private ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    private long mContactId;
    // The contact's LOOKUP_KEY
    private String mContactKey;
    // A content URI for the selected contact
    private Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    // Activity for permissions
    private ListContactsActivity listContactsActivity;

    /*
    CURSOR LOADER
     */
    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
    };

    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = { null };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listContactsActivity = (ListContactsActivity) context;
    }

    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View root = inflater.inflate(R.layout.fragment_list_contacts, container, false);

        // Gets the ListView from the View list of the parent activity
        mContactsList = (ListView) root.findViewById(R.id.fragment_list_contacts_listview);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Gets a CursorAdapter
        mCursorAdapter = new SimpleCursorAdapter(
                getContext(),
                R.layout.item_list_contacts,
                null,
                FROM_COLUMNS, TO_IDS,
                0);

        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);

        // Add listener for selection
        mContactsList.setOnItemClickListener(this);

        // Initializes the loader
        getLoaderManager().initLoader(0, null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /*
         * Makes search string into pattern and
         * stores it in the selection array
         */
        //mSelectionArgs[0] = "%" + mSearchString + "%";
        // Starts the query
        return new CursorLoader(
                getActivity(),
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null, //SELECTION,
                null, //mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Delete the reference to the existing Cursor
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View item, int position, long rowID) {
        // Get the Cursor
        Cursor cursor = ((SimpleCursorAdapter) parent.getAdapter()).getCursor();
        // Move to the selected contact
        cursor.moveToPosition(position);
        // Get the _ID value
        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        // Get the selected LOOKUP KEY
        mContactKey = cursor.getString(LOOKUP_KEY_INDEX);
        // Create the contact's content Uri
        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
        /*
         * You can use mContactUri as the content URI for retrieving
         * the details for a contact.
         */
        SelectBirthdayDialogFragment dialog = new SelectBirthdayDialogFragment();
        dialog.setOnClickListener(new SelectBirthdayDialogFragment.OnClickBirthdayListener() {
            @Override
            public void onClickPositiveButton(DateUnknownYear dateUnknownYear) {
                AddBirthdayToContactTask addBirthdayToContactTask = new AddBirthdayToContactTask(mContactId, dateUnknownYear, getActivity());
                addBirthdayToContactTask.setCallbackAddBirthdayToContact(ListContactsFragment.this);
                addBirthdayToContactTask.execute();
            }

            @Override
            public void onClickNegativeButton(DateUnknownYear selectedDate) {
            }
        });
        dialog.show(getChildFragmentManager(), "NoticeDialogFragment");
    }

    /*TODO javadoc */
    public InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContext().getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    @Override
    public void afterAddBirthdayInDatabase(Exception exception) {
        String message;
        if(exception == null)
            message = getString(R.string.activity_list_contacts_success_add_birthday);
        else {
            Log.e(TAG, exception.getMessage());
            message = getString(R.string.activity_list_contacts_error_add_birthday);
        }

        Snackbar infoSnackbar = Snackbar.make(getActivity().findViewById(R.id.activity_list_contacts_information),
                message, Snackbar.LENGTH_SHORT);
        infoSnackbar.show();
    }
}
