package com.kunzisoft.remembirthday.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.element.Reminder;
import com.kunzisoft.remembirthday.preference.PreferencesManager;


/**
 * Adapter who manage list of reminders
 */
public class ReminderCalendarNotificationsAdapter extends AbstractReminderAdapter<Reminder, ReminderViewHolder> {

    public ReminderCalendarNotificationsAdapter(Context context, DateUnknownYear anniversary) {
        super(context, anniversary);
    }

    @Override
    public void addDefaultItem() {
        int[] defaultTime = PreferencesManager.getDefaultTime(context);
        addReminder(new Reminder(anniversary.getDate(), defaultTime[0], defaultTime[1], 0));
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_reminder_notifs, parent, false);
        return new ReminderViewHolder(itemView);
    }
}
