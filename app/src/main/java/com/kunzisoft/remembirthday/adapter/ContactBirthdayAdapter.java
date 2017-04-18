package com.kunzisoft.remembirthday.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.Utility;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;

/**
 * Adapter linked to contacts with birthday for data feeding
 */
public class ContactBirthdayAdapter extends ContactAdapter<ContactBirthdayViewHolder> {

    private static final String TAG = "ContactBirthdayAdapter";

    private int contactStartDateColIdx;
    private int contactTypeColIdx;
    private final int contactTypeBirthdayDataIdx = ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
    private final int contactTypeAnniversaryDataIdx = ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY;

    public ContactBirthdayAdapter(Context context) {
        super(context);
    }

    @Override
    public void swapCursor(Cursor cursor) {
        super.swapCursor(cursor);
        this.contactStartDateColIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        this.contactTypeColIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE);
    }

    @Override
    public ContactBirthdayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_contacts_birthday, parent, false);
        return new ContactBirthdayViewHolder(itemListBuddyView);
    }

    @Override
    protected Contact getItemFromCursor(Cursor cursor) {
        Contact contact = super.getItemFromCursor(cursor);
        DateUnknownYear dateUnknownYear = null;
        try {
            switch(cursor.getInt(contactTypeColIdx)) {
                case contactTypeBirthdayDataIdx:
                    dateUnknownYear = DateUnknownYear.stringToDateWithKnownYear(cursor.getString(contactStartDateColIdx));
                    break;
                case contactTypeAnniversaryDataIdx:
                    dateUnknownYear = DateUnknownYear.stringToDateWithUnknownYear(cursor.getString(contactStartDateColIdx));
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Birthday can't be extract : " + e.getMessage());
        } finally {
            if(dateUnknownYear != null)
                contact.setBirthday(dateUnknownYear);
        }

        return contact;
    }

    @Override
    protected void assignDataToView(ContactBirthdayViewHolder holder, Contact contact) {
        super.assignDataToView(holder, contact);

        if(contact.getBirthday().containsYear()) {
            holder.age.setVisibility(View.VISIBLE);
            holder.age.setText(String.valueOf(contact.getAge()));
        } else {
            holder.age.setVisibility(View.INVISIBLE);
            holder.age.setText("");
        }
        holder.birthday.setText(contact.getBirthday().toString());
        Utility.assignDaysRemainingInTextView(holder.daysLeft, contact.getBirthdayDaysRemaining());
    }

}
