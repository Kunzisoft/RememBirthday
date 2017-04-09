package com.kunzisoft.remembirthday.adapter;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.Contact;

/**
 * Adapter linked to contacts with birthday for data feeding
 */
public class ContactAdapter<T extends ContactViewHolder> extends RecyclerView.Adapter<T>{

    private static final String TAG = "ContactBirthdayAdapter";

    private OnClickItemContactListener onClickItemContactListener;

    private Cursor cursor;
    protected int contactIdColIdx, contactNameColIdx;

    /**
     * Change cursor implementation for retrieving data
     * @param cursor New cursor
     * @return The old cursor
     */
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        this.contactIdColIdx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        this.contactNameColIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
    }

    public void resetCursor() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_contacts, parent, false);
        return (T) new ContactViewHolder(itemListBuddyView);
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        cursor.moveToPosition(position);

        Contact currentContact = getItemFromCursor(cursor);
        assignDataToView(holder, currentContact);

        if(onClickItemContactListener != null) {
            holder.container.setOnClickListener(new BufferContactClickListener(currentContact, position));
        }
    }

    /**
     * MUST BE REDEFINED
     * Must return a new item based on cursor data
     * @param cursor The cursor at the correct position
     * @return The new item created
     */
    protected Contact getItemFromCursor(Cursor cursor) {
        return new Contact(cursor.getLong(contactIdColIdx),
                cursor.getString(contactNameColIdx));
    }

    /**
     * MUST BE REDEFINED <br />
     * Method used to link the ViewHolder to the item data
     * @param holder The ViewHolder
     * @param contact The item
     */
    protected void assignDataToView(T holder, Contact contact) {
        // TODO icon
        //holder.icon.
        holder.name.setText(contact.getName());
    }

    /**
     * Retrieves the listener that handles the event on the click of the item
     * @return The listener
     */
    public OnClickItemContactListener getOnClickItemContactListener() {
        return onClickItemContactListener;
    }

    /**
     * Add click contactBirthday listener to each item
     * @param onClickItemContactListener Listener who defined the `onItemContactClick` method
     */
    public void setOnClickItemContactListener(OnClickItemContactListener onClickItemContactListener) {
        this.onClickItemContactListener = onClickItemContactListener;
    }

    public void setItemChecked(Contact contact) {
        // TODO highlight
    }

    @Override
    public int getItemCount() {
        if(cursor!=null)
            return cursor.getCount();
        else
            return 0;
    }

    /**
     * Class manager for add contact and view in listener
     */
    private class BufferContactClickListener implements View.OnClickListener {

        private Contact contact;
        private int position;

        BufferContactClickListener(Contact contact, int position) {
            this.contact = contact;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            onClickItemContactListener.onItemContactClick(view, contact, cursor, position);
        }
    }
}
