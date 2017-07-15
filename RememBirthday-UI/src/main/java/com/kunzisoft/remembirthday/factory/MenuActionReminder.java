package com.kunzisoft.remembirthday.factory;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 13/07/17.
 */

public class MenuActionReminder extends MenuAction{

    public static final int ITEM_ID = 1646954;

    public MenuActionReminder(STATE state) {
        super(R.string.reminder_title,
                R.drawable.ic_alarm_white_24dp,
                state);
    }

    public MenuActionReminder() {
        this(STATE.ACTIVE);
    }

    @Override
    public int getItemId() {
        return ITEM_ID;
    }
}
