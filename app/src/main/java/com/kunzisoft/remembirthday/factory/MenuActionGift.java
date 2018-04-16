package com.kunzisoft.remembirthday.factory;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 13/07/17.
 */

public class MenuActionGift extends MenuAction{

    public static final int ITEM_ID = 2561957;

    public MenuActionGift(STATE state) {
        super(R.string.gift_title,
                R.drawable.ic_gift_white_24dp,
                state);
    }

    public MenuActionGift() {
        this(STATE.ACTIVE);
    }

    @Override
    public int getItemId() {
        return ITEM_ID;
    }
}
