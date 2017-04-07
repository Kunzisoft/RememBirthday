package com.kunzisoft.remembirthday.adapter;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.Utility;
import com.kunzisoft.remembirthday.element.ContactBirthday;
import com.kunzisoft.remembirthday.element.DateUnknownYear;

/**
 * Adapter linked to contacts with birthday for data feeding
 */
public class ContactBirthdayAdapter extends RecyclerView.Adapter<ContactBirthdayViewHolder>{

    private static final String TAG = "ContactBirthdayAdapter";

    private OnClickItemBuddyListener onClickItemBuddyListener;

    private Cursor cursor;
    private final int contactIdColIdx, contactNameColIdx, contactBirthdayColIdx;

    //TODO change generic
    public ContactBirthdayAdapter(Cursor cursor) {
        this.cursor = cursor;
        this.contactIdColIdx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        this.contactNameColIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        this.contactBirthdayColIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
    }

    @Override
    public ContactBirthdayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_contacts_birthday, parent, false);
        return new ContactBirthdayViewHolder(itemListBuddyView);
    }

    @Override
    public void onBindViewHolder(ContactBirthdayViewHolder holder, int position) {
        cursor.moveToPosition(position);

        DateUnknownYear dateUnknownYear;
        try {
            dateUnknownYear = DateUnknownYear.stringToDateWithKnownYear(cursor.getString(contactBirthdayColIdx));
        } catch (Exception e) {
            Log.e(TAG, "Birthday can't be extract : " + e.getMessage());
            dateUnknownYear = DateUnknownYear.getDefault();
        }

        ContactBirthday currentContactBirthday = new ContactBirthday(cursor.getLong(contactIdColIdx),
                cursor.getString(contactNameColIdx),
                dateUnknownYear);

        // TODO icon
        //holder.icon.
        holder.name.setText(currentContactBirthday.getName());
        holder.age.setText(String.valueOf(currentContactBirthday.getAge()));
        Utility.assignDaysRemainingInTextView(holder.daysLeft, currentContactBirthday.getBirthdayDaysRemaining());

        if(onClickItemBuddyListener != null) {
            holder.container.setOnClickListener(new BufferContactClickListener(currentContactBirthday));
        }
    }

    public OnClickItemBuddyListener getOnClickItemBuddyListener() {
        return onClickItemBuddyListener;
    }

    /**
     * Add click contactBirthday listener to each item
     * @param onClickItemBuddyListener Listener who defined the `onItemBuddyClick` method
     */
    public void setOnClickItemBuddyListener(OnClickItemBuddyListener onClickItemBuddyListener) {
        this.onClickItemBuddyListener = onClickItemBuddyListener;
    }

    public void setItemChecked(ContactBirthday contactBirthday) {
        // TODO
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    /**
     * Listener when a click on contactBirthday item is performed
     */
    public interface OnClickItemBuddyListener {
        void onItemBuddyClick(View view, ContactBirthday contactBirthday);
    }


    private class BufferContactClickListener implements View.OnClickListener {

        private ContactBirthday contactBirthday;

        BufferContactClickListener(ContactBirthday contactBirthday) {
            this.contactBirthday = contactBirthday;
        }

        @Override
        public void onClick(View view) {
            onClickItemBuddyListener.onItemBuddyClick(view, contactBirthday);
        }
    }
}
