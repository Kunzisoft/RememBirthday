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
public class ReminderNotificationsAdapter extends AbstractReminderAdapter<Reminder, ReminderViewHolder> {

    public ReminderNotificationsAdapter(Context context, DateUnknownYear anniversary) {
        super(context, anniversary);

        // Build default elements
        int[] defaultDays = PreferencesManager.getDefaultDays(context);
        // TODO get items from saved element
        for (int day : defaultDays) {
            addDefaultItem(day);
        }
    }

    @Override
    public void addDefaultItem(int deltaDay) {
        int[] defaultTime = PreferencesManager.getDefaultTime(context);
        addReminder(new Reminder(anniversary.getDate(), defaultTime[0], defaultTime[1], deltaDay));
    }

    @Override
    public void addDefaultItem() {
        addDefaultItem(0);
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_reminder_notifs, parent, false);
        return new AutoMessageViewHolder(itemView);
    }
}
