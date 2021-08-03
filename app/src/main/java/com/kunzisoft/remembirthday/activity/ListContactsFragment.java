package com.kunzisoft.remembirthday.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.adapter.ContactAdapter;
import com.kunzisoft.remembirthday.adapter.OnClickItemContactListener;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.provider.ContactLoader;

/**
 * Fragment that retrieves and displays the list of contacts
 */
public class ListContactsFragment extends AbstractListContactsFragment
        implements OnClickItemContactListener {

    private static final String TAG = "ListContactsFragment";

    @Override
    protected ContactLoader initializeLoader() {
        return new ContactLoader.ContactBaseLoader(getContext());
    }

    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_list_contacts, container, false);

        // Gets the ListView from the View list of the parent activity
        contactsListView = (RecyclerView) rootView.findViewById(R.id.fragment_list_contacts_recyclerview_contacts);
        contactsListView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contactsListView.setLayoutManager(linearLayoutManager);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Put the result Cursor in the adapter for the ListView
        contactAdapter = new ContactAdapter(getContext());
        contactAdapter.setOnClickItemContactListener(this);
        contactsListView.setAdapter(contactAdapter);
    }

    @Override
    public void onItemContactClick(View view, Contact contact, Cursor cursor, int position) {
        // Get raw contact id if undefined
        long contactRawId = ContactLoader.assignRawContactIdToContact(getContext(), contact);
        // TODO Verify if already a birthday
        contact.setRawId(contactRawId);

        try {
            ((AnniversaryDialogOpen) getActivity()).openAnniversaryDialogSelection(contact);
        } catch(ClassCastException e) {
            Log.e(TAG, "Wrong activity, must be 'AnniversaryDialogOpen'");
        }
    }
}
