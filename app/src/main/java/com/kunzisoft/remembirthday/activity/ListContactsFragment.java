package com.kunzisoft.remembirthday.activity;

import android.annotation.SuppressLint;
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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.adapter.ContactAdapter;
import com.kunzisoft.remembirthday.adapter.OnClickItemContactListener;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.task.AddBirthdayToContactTask;

/**
 * Fragment that retrieves and displays the list of contacts
 */
public class ListContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnClickItemContactListener, AddBirthdayToContactTask.CallbackAddBirthdayToContact {

    private static final String TAG = "ListContactsFragment";

    private Contact currentCheckContact;

    private RecyclerView contactsListView;
    private ContactAdapter contactAdapter;

    // Connexion to database
    private static final Uri URI = ContactsContract.Data.CONTENT_URI;
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
    };
    //private static final String SELECTION = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?";
    private static final String SELECTION =
            ContactsContract.Data.MIMETYPE + "= ? AND (" +
                    ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                    ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + " OR " +
                    ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                    ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY +
                    " ) ";
    String[] selectionArgs = new String[] {
            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
    };

    // The contact's _ID value
    private long mContactId;
    // The contact's LOOKUP_KEY
    private String mContactKey;
    // A content URI for the selected contact
    private Uri mContactUri;


    // Dialog for birthday selection
    private SelectBirthdayDialogFragment dialogSelection;


    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_list_contacts, container, false);

        // Gets the ListView from the View list of the parent activity
        contactsListView = (RecyclerView) rootView.findViewById(R.id.fragment_list_contacts_recyclerview_contacts);
        contactsListView.setHasFixedSize(true);
        contactsListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contactsListView.setLayoutManager(linearLayoutManager);
        contactsListView.setAdapter(contactAdapter);

        // Initialize dialog for birthday selection
        dialogSelection = new SelectBirthdayDialogFragment();
        dialogSelection.setOnClickListener(new SelectBirthdayDialogFragment.OnClickBirthdayListener() {
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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Put the result Cursor in the adapter for the ListView
        //TODO init here (onLoadFinish)

        // Initializes the loader
        getLoaderManager().initLoader(0, null, this);
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

    @Override
    public void onItemContactClick(View view, Contact contact, Cursor cursor, int position) {
        mContactId = contact.getId();
        dialogSelection.show(getChildFragmentManager(), "NoticeDialogFragment");
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
                URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        contactAdapter = new ContactAdapter(cursor);
        contactsListView.setAdapter(contactAdapter);
        contactAdapter.setOnClickItemContactListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Delete the reference to the existing Cursor
        contactAdapter = null;
    }
}
