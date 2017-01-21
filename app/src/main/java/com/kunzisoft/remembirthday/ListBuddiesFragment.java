package com.kunzisoft.remembirthday;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.adapter.BuddyAdapter;
import com.kunzisoft.remembirthday.element.Buddy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by joker on 08/01/17.
 */

public class ListBuddiesFragment extends Fragment implements BuddyAdapter.OnClickItemBuddyListener{

    private final static String EXTRA_DUAL_PANEL = "EXTRA_DUAL_PANEL";
    private final static String TAG_FRAGMENT = "TAG_FRAGMENT";
    private static final String TAG = "ListBuddiesFragment";
    private Buddy currentCheckBuddy;

    private BuddyAdapter buddyAdapter;

    private boolean mDualPane;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO List users
        List<Buddy> listBuddy = new ArrayList<>();
        Buddy michel = new Buddy("michmich", new Date());
        listBuddy.add(michel);

        Buddy jeanmichel = new Buddy("jeanmichmich", new Date());
        listBuddy.add(jeanmichel);

        Buddy bernadette = new Buddy("berna dettes", new Date());
        listBuddy.add(bernadette);

        buddyAdapter = new BuddyAdapter(listBuddy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_list_buddies, container, false);

        // List buddies
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_list_buddies_recyclerview_buddies);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(buddyAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View detailsFrame = getActivity().findViewById(R.id.activity_buddy_container_details_fragment);
        mDualPane = (detailsFrame != null) && (detailsFrame.getVisibility() == View.VISIBLE);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            currentCheckBuddy = savedInstanceState.getParcelable(EXTRA_DUAL_PANEL);
        }

        if (mDualPane) {
            // Make sure our UI is in the correct state.
            showDetails(currentCheckBuddy);
        }

        buddyAdapter.setOnClickItemBuddyListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_DUAL_PANEL, currentCheckBuddy);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(Buddy buddy) {
        currentCheckBuddy = buddy;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            buddyAdapter.setItemChecked(buddy);

            // Make new fragment to show this selection.
            DetailsBuddyFragment detailsFragment = new DetailsBuddyFragment();
            detailsFragment.setBuddy(buddy);

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
            intent.putExtra(BuddyActivity.EXTRA_BUDDY, buddy);
            startActivity(intent);
        }
    }

    @Override
    public void onItemBuddyClick(View view, Buddy buddy) {
        showDetails(buddy);
    }

    /**
     * Get list of contacts with id, name and birthday
     * @return
     */
    private List<Buddy> getContactsBirthdays() {
        List<Buddy> buddies = new ArrayList<>();

        Uri uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.START_DATE
        };

        String where =
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
        String[] selectionArgs = new String[] {
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, where, selectionArgs, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                // Iterate through the cursor
                do {
                    // Get the contacts name
                    long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.CONTACT_ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String date = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date dateBuddy = sdf.parse(date);
                        Buddy newBuddy = new Buddy(id, name, dateBuddy);
                        buddies.add(newBuddy);

                    } catch (ParseException e) {
                        Log.e(TAG, e.getMessage());
                    }

                } while (cursor.moveToNext());
            }
            // Close the cursor
            cursor.close();
        }
        return buddies;
    }
}
