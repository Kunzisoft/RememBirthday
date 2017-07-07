package com.kunzisoft.remembirthday.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
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
import com.kunzisoft.remembirthday.preference.PreferencesManager;

/**
 * Created by joker on 08/01/17.
 */
public class ListBuddiesFragment extends AbstractListContactsFragment implements OnClickItemContactListener {

    public final static String TAG_DETAILS_FRAGMENT = "TAG_DETAILS_FRAGMENT";
    private final static String EXTRA_DUAL_PANEL = "EXTRA_DUAL_PANEL";
    private static final String TAG = "ListBuddiesFragment";
    private Contact currentCheckContact;

    private boolean mDualPane;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Redefined Query
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
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + //" OR " +
                        //ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        //ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY +
                        " ) ";
        selectionArgs = new String[] {
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };
        // Define the default sort
        contactSort = PreferencesManager.getDefaultContactSort(getContext());

        View rootView = inflater.inflate(R.layout.fragment_list_buddies, container, false);

        // List buddies
        contactsListView = (RecyclerView) rootView.findViewById(R.id.fragment_list_buddies_recyclerview_buddies);
        contactsListView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contactsListView.setLayoutManager(linearLayoutManager);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Put the result Cursor in the adapter for the ListView
        contactAdapter = new ContactBirthdayAdapter(getContext());
        contactAdapter.setOnClickItemContactListener(this);
        contactsListView.setAdapter(contactAdapter);

        // Manage dual panel
        View detailsFrame = getActivity().findViewById(R.id.activity_buddy_container_details_fragment);
        mDualPane = (detailsFrame != null) && (detailsFrame.getVisibility() == View.VISIBLE);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            currentCheckContact = savedInstanceState.getParcelable(EXTRA_DUAL_PANEL);
        }

        if (mDualPane) {
            // Make sure our UI is in the correct state.
            showDetails(currentCheckContact);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_DUAL_PANEL, currentCheckContact);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        super.onLoadFinished(loader, cursor);

        // Check the first element in new thread
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(currentCheckContact == null) {
                    showFirstContact();
                }
            }
        });
    }

    /**
     * Show the first contact by select them on the list and show its details
     */
    public void showFirstContact() {
        // init currentContact to first in the list if we are un dualPane
        if (mDualPane) {
            currentCheckContact = contactAdapter.getFirst();
            contactAdapter.setItemChecked(0);
            showDetails(currentCheckContact);
        }
    }

    /**
     * Helper function to showMessage the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(Contact contact) {
        currentCheckContact = contact;

        if (mDualPane) {
            // Assign currentContact to activity
            AbstractBuddyActivity abstractBuddyActivity = (AbstractBuddyActivity) getActivity();
            abstractBuddyActivity.setContactSelected(contact);
            abstractBuddyActivity.attachDialogListener(contact);

            // Make new fragment to showMessage this selection.
            DetailsBuddyFragment detailsFragment = new DetailsBuddyFragment();
            detailsFragment.setBuddy(contact);

            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            if(getFragmentManager().findFragmentByTag(TAG_DETAILS_FRAGMENT) == null)
                fragmentTransaction.add(R.id.activity_buddy_container_details_fragment, detailsFragment, TAG_DETAILS_FRAGMENT);
            else
                fragmentTransaction.replace(R.id.activity_buddy_container_details_fragment, detailsFragment, TAG_DETAILS_FRAGMENT);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsBuddyActivity.class);
            intent.putExtra(BuddyActivity.EXTRA_BUDDY, contact);
            startActivity(intent);
        }
    }

    @Override
    public void onItemContactClick(View view, Contact contact, Cursor cursor, int position) {
        if(mDualPane) {
            contactAdapter.setItemChecked(position);
        }
        showDetails(contact);
    }
}
