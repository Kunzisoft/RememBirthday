package com.kunzisoft.remembirthday.adapter.observer;

import android.content.Context;
import android.widget.Toast;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.adapter.AbstractReminderAdapter;
import com.kunzisoft.remembirthday.element.Reminder;

import java.util.List;

/**
 * Created by joker on 07/08/17.
 */

public class ReminderToastObserver implements AbstractReminderAdapter.ReminderDataObserver<Reminder> {

    private Context context;

    public ReminderToastObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onReminderAdded(Reminder reminder) {
        Toast.makeText(context, context.getString(R.string.reminder_added, reminder.getDate()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemindersAdded(List<Reminder> reminders) {

    }

    @Override
    public void onReminderUpdated(Reminder reminder) {
        Toast.makeText(context, context.getString(R.string.reminder_updated, reminder.getDate()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemindersUpdated(List<Reminder> reminders) {

    }

    @Override
    public void onReminderDeleted(Reminder reminder) {
        Toast.makeText(context, context.getString(R.string.reminder_deleted), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemindersDeleted(List<Reminder> reminders) {

    }
}
