package com.kunzisoft.remembirthday.factory;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.Contact;

/**
 * Created by joker on 13/07/17.
 */

public class MenuActionReminder extends MenuAction{

    public MenuActionReminder() {
        super(R.string.reminder_title,
                R.drawable.ic_alarm_white_24dp);
    }

    public MenuActionReminder(boolean active) {
        super(R.string.reminder_title,
                R.drawable.ic_alarm_white_24dp,
                active);
    }

    @Override
    public void doAction(Context context,
                         FragmentManager fragmentManager,
                         Contact contact) {
        super.doAction(context, fragmentManager, contact);

    }
}
