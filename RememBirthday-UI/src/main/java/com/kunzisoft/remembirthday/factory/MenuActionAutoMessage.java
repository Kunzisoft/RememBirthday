package com.kunzisoft.remembirthday.factory;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.Contact;

/**
 * Created by joker on 13/07/17.
 */

public class MenuActionAutoMessage extends MenuAction{

    public MenuActionAutoMessage() {
        super(R.string.auto_sms_title,
                R.drawable.ic_text_clock_white_24dp);
    }

    public MenuActionAutoMessage(boolean active) {
        super(R.string.auto_sms_title,
                R.drawable.ic_text_clock_white_24dp,
                active);
    }

    @Override
    public void doAction(Context context,
                         FragmentManager fragmentManager,
                         Contact contact) {
        super.doAction(context, fragmentManager, contact);
    }
}
