package com.kunzisoft.remembirthday.factory;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 13/07/17.
 */

public class MenuActionAutoVoice extends MenuAction{

    public static final int ITEM_ID = 1691968;

    public MenuActionAutoVoice(boolean active) {
        super(R.string.auto_sound_title,
                R.drawable.ic_event_voice_24dp,
                active);
    }

    public MenuActionAutoVoice() {
        this(true);
    }

    @Override
    public int getItemId() {
        return ITEM_ID;
    }
}
