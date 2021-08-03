package com.kunzisoft.remembirthday.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 13/07/17.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder {

    public View container;
    public AppCompatImageView image;
    public TextView title;

    public MenuViewHolder(View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.item_list_menu_container);
        image = (AppCompatImageView) itemView.findViewById(R.id.item_list_menu_image);
        title = (TextView) itemView.findViewById(R.id.item_list_menu_title);
    }
}
