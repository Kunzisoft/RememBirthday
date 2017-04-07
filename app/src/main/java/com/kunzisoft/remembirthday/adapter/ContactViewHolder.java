package com.kunzisoft.remembirthday.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 07/04/17.
 */
public class ContactViewHolder extends RecyclerView.ViewHolder {

    public ViewGroup container;

    public ImageView icon;
    public TextView name;

    ContactViewHolder(View itemView) {
        super(itemView);
        container = (ViewGroup) itemView.findViewById(R.id.item_list_contacts_container);

        icon = (ImageView) itemView.findViewById(R.id.item_list_contact_icon);
        name = (TextView) itemView.findViewById(R.id.item_list_contact_name);
    }
}
