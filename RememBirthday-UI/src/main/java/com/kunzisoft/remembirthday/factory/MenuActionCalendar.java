package com.kunzisoft.remembirthday.factory;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.Utility;
import com.kunzisoft.remembirthday.element.Contact;

/**
 * Created by joker on 13/07/17.
 */

public class MenuActionCalendar extends MenuAction{

    public MenuActionCalendar() {
        super(R.string.calendar_title, R.drawable.ic_event_note_white_24dp);
    }

    @Override
    public void doAction(Context context,
                         FragmentManager fragmentManager,
                         Contact contact) {
        Utility.openCalendarAt(context, contact.getNextBirthday());
    }
}
