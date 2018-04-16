package com.kunzisoft.remembirthday.adapter.observer;

import android.content.Context;
import android.widget.Toast;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.adapter.AbstractReminderAdapter;
import com.kunzisoft.remembirthday.element.AutoMessage;
import com.kunzisoft.remembirthday.element.Reminder;

import java.util.List;

/**
 * Created by joker on 07/08/17.
 */

public class AutoSmsToastObserver implements AbstractReminderAdapter.ReminderDataObserver<AutoMessage> {

    private Context context;

    public AutoSmsToastObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onReminderAdded(AutoMessage reminder) {
        Toast.makeText(context, context.getString(R.string.auto_sms_added, reminder.getDate()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemindersAdded(List<AutoMessage> reminders) {

    }

    @Override
    public void onReminderUpdated(AutoMessage reminder) {
        Toast.makeText(context, context.getString(R.string.auto_sms_updated, reminder.getDate()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemindersUpdated(List<AutoMessage> reminders) {

    }

    @Override
    public void onReminderDeleted(AutoMessage reminder) {
        Toast.makeText(context, context.getString(R.string.auto_sms_deleted), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemindersDeleted(List<AutoMessage> reminders) {

    }
}
