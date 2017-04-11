package com.kunzisoft.remembirthday.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.adapter.ContactAdapter;

/**
 * Fragment that retrieves and displays the list of contacts
 */
public abstract class AbstractListContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "AbstractListContactsFragment";

    protected RecyclerView contactsListView;
    protected ContactAdapter contactAdapter;

    // Connexion to content provider
    protected Uri uri = ContactsContract.Contacts.CONTENT_URI;
    protected String[] projection = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
    };
    protected String selection = null;
    protected String[] selectionArgs = null;
    protected String sortOrder = null;

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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initializes the loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Starts the query
        return new CursorLoader(
                getActivity(),
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        contactAdapter.swapCursor(cursor);
        contactAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        contactAdapter.resetCursor();
    }
}
