package com.kunzisoft.remembirthday.adapter;

import android.view.View;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 07/04/17.
 */

public class ContactBirthdayViewHolder extends ContactViewHolder {

    public TextView age;
    public TextView daysLeft;

    ContactBirthdayViewHolder(View itemView) {
        super(itemView);
        age = (TextView) itemView.findViewById(R.id.item_list_contact_age);
        daysLeft = (TextView) itemView.findViewById(R.id.item_list_contact_days_left);
    }
}
