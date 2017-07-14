package com.kunzisoft.remembirthday.factory;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 13/07/17.
 */

public class MenuActionAutoMessage extends MenuAction{

    public static final int ITEM_ID = 1684968;

    public MenuActionAutoMessage(boolean active) {
        super(R.string.auto_message_title,
                R.drawable.ic_text_clock_white_24dp,
                active);
    }

    public MenuActionAutoMessage() {
        this(true);
    }

    @Override
    public int getItemId() {
        return ITEM_ID;
    }
}
