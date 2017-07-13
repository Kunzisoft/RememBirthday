package com.kunzisoft.remembirthday.factory;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 13/07/17.
 */

public class MenuActionMessage extends MenuAction{

    public static final int ITEM_ID = 1667954;

    public MenuActionMessage() {
        super(R.string.message_title,
                R.drawable.ic_message_white_24dp, false);
    }

    @Override
    public int getItemId() {
        return ITEM_ID;
    }
}
