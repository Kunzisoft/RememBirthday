package com.kunzisoft.remembirthday.adapter.observer;

import com.kunzisoft.remembirthday.adapter.AbstractReminderAdapter;
import com.kunzisoft.remembirthday.element.AutoMessage;

import java.util.List;

/**
 * Created by joker on 18/08/17.
 */

public class AutoSmsDatabaseObserver implements AbstractReminderAdapter.ReminderDataObserver<AutoMessage> {
    @Override
    public void onReminderAdded(AutoMessage reminder) {

    }

    @Override
    public void onRemindersAdded(List<AutoMessage> reminders) {

    }

    @Override
    public void onReminderUpdated(AutoMessage reminder) {

    }

    @Override
    public void onRemindersUpdated(List<AutoMessage> reminders) {

    }

    @Override
    public void onReminderDeleted(AutoMessage reminder) {

    }

    @Override
    public void onRemindersDeleted(List<AutoMessage> reminders) {

    }
}
