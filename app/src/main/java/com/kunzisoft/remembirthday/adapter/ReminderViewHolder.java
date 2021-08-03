package com.kunzisoft.remembirthday.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 01/07/17.
 */

public class ReminderViewHolder extends RecyclerView.ViewHolder {

    ImageView saveButton;
    ImageView deleteButton;
    TextView dateNotification;
    Spinner unitsBefore;

    public ReminderViewHolder(View itemView) {
        super(itemView);
        saveButton = (ImageView) itemView.findViewById(R.id.item_list_reminder_button_save);
        deleteButton = (ImageView) itemView.findViewById(R.id.item_list_reminder_button_delete);
        dateNotification = (TextView) itemView.findViewById(R.id.item_list_reminder_hour_notification);
        unitsBefore = (Spinner) itemView.findViewById(R.id.item_list_reminder_units_before);
    }
}
