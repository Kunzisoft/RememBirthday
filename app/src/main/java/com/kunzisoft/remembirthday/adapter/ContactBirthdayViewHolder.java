package com.kunzisoft.remembirthday.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;

/**
 * The ViewHolder used to manage the views of a contact
 */
class ContactBirthdayViewHolder extends ContactViewHolder {

    TextView age;
    TextView daysLeft;

    ContactBirthdayViewHolder(View itemView) {
        super(itemView);
        age = (TextView) itemView.findViewById(R.id.item_list_contact_age);
        daysLeft = (TextView) itemView.findViewById(R.id.item_list_contact_days_left);
    }
}
