package com.kunzisoft.remembirthday.adapter;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.Utility;
import com.kunzisoft.remembirthday.element.Buddy;
import com.kunzisoft.remembirthday.element.DateUnknownYear;

import java.text.ParseException;

/**
 * Adapter linked to buddies for data feeding
 */
public class BuddiesAdapter extends RecyclerView.Adapter<BuddiesAdapter.BuddyViewHolder>{

    private static final String TAG = "BuddiesAdapter";

    private OnClickItemBuddyListener onClickItemBuddyListener;

    private Cursor cursor;
    private final int contactIdColIdx, contactNameColIdx, contactBirthdayColIdx;

    //TODO change generic
    public BuddiesAdapter(Cursor cursor) {
        this.cursor = cursor;
        this.contactIdColIdx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        this.contactNameColIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        this.contactBirthdayColIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
    }

    @Override
    public BuddiesAdapter.BuddyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_buddies, parent, false);
        return new BuddyViewHolder(itemListBuddyView);
    }

    @Override
    public void onBindViewHolder(BuddyViewHolder holder, int position) {
        cursor.moveToPosition(position);

        DateUnknownYear dateUnknownYear;
        try {
            dateUnknownYear= DateUnknownYear.stringToDateWithKnownYear(cursor.getString(contactBirthdayColIdx));
        } catch (ParseException e) {
            Log.e(TAG, "Birthday can't be extract : " + e.getMessage());
            dateUnknownYear = DateUnknownYear.getDefault();
        }

        Buddy currentBuddy = new Buddy(cursor.getLong(contactIdColIdx),
                cursor.getString(contactNameColIdx),
                dateUnknownYear);

        // TODO icon
        //holder.icon.
        holder.name.setText(currentBuddy.getName());
        holder.age.setText(String.valueOf(currentBuddy.getAge()));
        Utility.assignDaysRemainingInTextView(holder.daysLeft, currentBuddy.getBirthdayDaysRemaining());

        if(onClickItemBuddyListener != null) {
            holder.container.setOnClickListener(new BufferBuddyClickListener(currentBuddy));
        }
    }

    public OnClickItemBuddyListener getOnClickItemBuddyListener() {
        return onClickItemBuddyListener;
    }

    /**
     * Add click buddy listener to each item
     * @param onClickItemBuddyListener Listener who defined the `onItemBuddyClick` method
     */
    public void setOnClickItemBuddyListener(OnClickItemBuddyListener onClickItemBuddyListener) {
        this.onClickItemBuddyListener = onClickItemBuddyListener;
    }

    public void setItemChecked(Buddy buddy) {
        // TODO
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    /**
     * Holder for buddy list
     */
    class BuddyViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup container;

        private ImageView icon;
        private TextView name;
        private TextView age;
        private TextView daysLeft;

        BuddyViewHolder(View itemView) {
            super(itemView);

            container = (ViewGroup) itemView.findViewById(R.id.buddy_item_container);

            icon = (ImageView) itemView.findViewById(R.id.buddy_icon);
            name = (TextView) itemView.findViewById(R.id.buddy_name);
            age = (TextView) itemView.findViewById(R.id.buddy_age);
            daysLeft = (TextView) itemView.findViewById(R.id.buddy_days_left);
        }
    }

    /**
     * Listener when a click on buddy item is performed
     */
    public interface OnClickItemBuddyListener {
        void onItemBuddyClick(View view, Buddy buddy);
    }

    private class BufferBuddyClickListener implements View.OnClickListener {

        private Buddy buddy;

        BufferBuddyClickListener(Buddy buddy) {
            this.buddy = buddy;
        }

        @Override
        public void onClick(View view) {
            onClickItemBuddyListener.onItemBuddyClick(view, buddy);
        }
    }
}
