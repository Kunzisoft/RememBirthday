package com.kunzisoft.remembirthday.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.adapter.ContactAdapter;
import com.kunzisoft.remembirthday.adapter.OnClickItemContactListener;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.task.AddBirthdayToContactTask;

/**
 * Fragment that retrieves and displays the list of contacts
 */
public class ListContactsFragment extends AbstractListContactsFragment
        implements AddBirthdayToContactTask.CallbackAddBirthdayToContact, OnClickItemContactListener {

    private static final String TAG = "ListContactsFragment";

    private static final String TAG_SELECT_DIALOG = "TAG_SELECT_DIALOG";

    // The contact's _ID value
    private long mContactId;

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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Put the result Cursor in the adapter for the ListView
        contactAdapter = new ContactAdapter();
        contactAdapter.setOnClickItemContactListener(this);
        contactsListView.setAdapter(contactAdapter);

        // Initialize dialog for birthday selection
        dialogSelection = (SelectBirthdayDialogFragment) getFragmentManager().findFragmentByTag(TAG_SELECT_DIALOG);
        if(dialogSelection == null)
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

        if(contact.hasBirthday()) {
            // TODO Show details activity
        } else
            dialogSelection.show(getChildFragmentManager(), TAG_SELECT_DIALOG);
    }
}
