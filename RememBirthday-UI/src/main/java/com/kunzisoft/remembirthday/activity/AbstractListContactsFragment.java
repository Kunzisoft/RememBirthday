package com.kunzisoft.remembirthday.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.adapter.ContactAdapter;
import com.kunzisoft.remembirthday.factory.ContactSort;
import com.kunzisoft.remembirthday.provider.ContactLoader;

/**
 * Fragment that retrieves and displays the list of contacts
 */
public abstract class AbstractListContactsFragment extends Fragment implements ContactLoader.LoaderContactCallbacks{

    private static final String TAG = "AbstractListContactsFragment";

    protected RecyclerView contactsListView;
    protected ContactAdapter contactAdapter;
    protected LinearLayoutManager linearLayoutManager;

    protected ContactLoader contactLoader;

    /**
     * Must return a Loader of contacts
     * @return ContactLoader
     */
    protected abstract ContactLoader initializeLoader();

    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_list_contacts, container, false);

        // Gets the ListView from the View list of the parent activity
        contactsListView = (RecyclerView) rootView.findViewById(R.id.fragment_list_contacts_recyclerview_contacts);
        contactsListView.setHasFixedSize(true);
        contactsListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contactsListView.setLayoutManager(linearLayoutManager);

        contactLoader = initializeLoader();
        contactLoader.setLoaderContactCallback(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initializes the loader
        getLoaderManager().initLoader(0, null, contactLoader);
    }

    @Override
    public void onContactLoadFinished(Loader<Cursor> loader, android.database.Cursor cursor) {
        contactAdapter.swapCursor(cursor);
        ContactSort contactSort = contactLoader.getContactSort();
        if(contactSort != null && contactSort.getContactComparator() != null)
            contactAdapter.sortElements(contactSort.getContactComparator());
        contactAdapter.notifyDataSetChanged();
    }

    @Override
    public void onContactLoaderReset(Loader<Cursor> loader) {
        contactAdapter.resetCursor();
    }

}
