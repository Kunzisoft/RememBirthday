package com.kunzisoft.remembirthday.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 01/07/17.
 */

public class ReminderViewHolder extends RecyclerView.ViewHolder {

    ImageView deleteButton;
    TextView dateNotification;

    public ReminderViewHolder(View itemView) {
        super(itemView);
        deleteButton = (ImageView) itemView.findViewById(R.id.item_list_reminder_button_delete);
        dateNotification = (TextView) itemView.findViewById(R.id.item_list_auto_message_date_notification);
    }
}
