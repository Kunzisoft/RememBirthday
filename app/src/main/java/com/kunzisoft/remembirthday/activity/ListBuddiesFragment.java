package com.kunzisoft.remembirthday.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.adapter.ContactBirthdayAdapter;
import com.kunzisoft.remembirthday.adapter.OnClickItemContactListener;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.ContactBirthday;

/**
 * Created by joker on 08/01/17.
 */
public class ListBuddiesFragment extends Fragment implements OnClickItemContactListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private final static String EXTRA_DUAL_PANEL = "EXTRA_DUAL_PANEL";
    private final static String TAG_FRAGMENT = "TAG_FRAGMENT";
    private static final String TAG = "ListBuddiesFragment";
    private ContactBirthday currentCheckContactBirthday;

    private RecyclerView buddiesListView;
    private ContactBirthdayAdapter contactBirthdayAdapter;

    private boolean mDualPane;

    // Connexion to database
    private static final Uri URI = ContactsContract.Data.CONTENT_URI;
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Event.START_DATE,
            ContactsContract.CommonDataKinds.Event.TYPE
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_list_buddies, container, false);

        // List buddies
        buddiesListView = (RecyclerView) rootView.findViewById(R.id.fragment_list_buddies_recyclerview_buddies);
        buddiesListView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        buddiesListView.setLayoutManager(linearLayoutManager);
        buddiesListView.setAdapter(contactBirthdayAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View detailsFrame = getActivity().findViewById(R.id.activity_buddy_container_details_fragment);
        mDualPane = (detailsFrame != null) && (detailsFrame.getVisibility() == View.VISIBLE);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            currentCheckContactBirthday = savedInstanceState.getParcelable(EXTRA_DUAL_PANEL);
        }

        if (mDualPane) {
            // Make sure our UI is in the correct state.
            showDetails(currentCheckContactBirthday);
        }

        // Put the result Cursor in the adapter for the ListView
        //TODO init here (onLoadFinish)

        // Initializes the loader for contacts
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_DUAL_PANEL, currentCheckContactBirthday);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(ContactBirthday contactBirthday) {
        currentCheckContactBirthday = contactBirthday;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            contactBirthdayAdapter.setItemChecked(contactBirthday);

            // Make new fragment to show this selection.
            DetailsBuddyFragment detailsFragment = new DetailsBuddyFragment();
            detailsFragment.setBuddy(contactBirthday);

            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            if(getFragmentManager().findFragmentByTag(TAG_FRAGMENT) == null)
                fragmentTransaction.add(R.id.activity_buddy_container_details_fragment, detailsFragment, TAG_FRAGMENT);
            else
                fragmentTransaction.replace(R.id.activity_buddy_container_details_fragment, detailsFragment, TAG_FRAGMENT);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsBuddyActivity.class);
            intent.putExtra(BuddyActivity.EXTRA_BUDDY, contactBirthday);
            startActivity(intent);
        }
    }

    @Override
    public void onItemContactClick(View view, Contact contact, Cursor cursor, int position) {
        showDetails((ContactBirthday) contact);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Starts the query
        return new CursorLoader(
                getActivity(),
                URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        contactBirthdayAdapter = new ContactBirthdayAdapter(data);
        buddiesListView.setAdapter(contactBirthdayAdapter);
        contactBirthdayAdapter.setOnClickItemContactListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        contactBirthdayAdapter = null;
    }
}
