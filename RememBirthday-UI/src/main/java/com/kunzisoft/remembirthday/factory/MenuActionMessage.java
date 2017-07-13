package com.kunzisoft.remembirthday.factory;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.Contact;

/**
 * Created by joker on 13/07/17.
 */

public class MenuActionMessage extends MenuAction{

    public MenuActionMessage() {
        super(R.string.message_title,
                R.drawable.ic_message_white_24dp, false);
    }

    @Override
    public void doAction(Context context,
                         FragmentManager fragmentManager,
                         Contact contact) {
        super.doAction(context, fragmentManager, contact);

    }
}
