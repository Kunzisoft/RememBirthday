package com.kunzisoft.remembirthday.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;

/**
 * The ViewHolder used to manage the views of a contact
 */
class ContactViewHolder extends RecyclerView.ViewHolder {

    ViewGroup container;
    ImageView icon;
    TextView name;

    ContactViewHolder(View itemView) {
        super(itemView);
        container = (ViewGroup) itemView.findViewById(R.id.item_list_contacts_container);
        icon = (ImageView) itemView.findViewById(R.id.item_list_contact_icon);
        name = (TextView) itemView.findViewById(R.id.item_list_contact_name);
    }
}
