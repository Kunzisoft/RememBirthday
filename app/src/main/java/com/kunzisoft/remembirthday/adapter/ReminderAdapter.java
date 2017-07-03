package com.kunzisoft.remembirthday.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.element.Reminder;


/**
 * Adapter who manage list of reminders
 */
public class ReminderAdapter extends AbstractReminderAdapter<Reminder, ReminderViewHolder> {

    public ReminderAdapter(Context context, DateUnknownYear anniversary) {
        super(context, anniversary);
    }

    @Override
    public void addDefaultItem() {
        addReminder(new Reminder(anniversary.getDate()));
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_reminders, parent, false);
        return new AutoMessageViewHolder(itemView);
    }
}
