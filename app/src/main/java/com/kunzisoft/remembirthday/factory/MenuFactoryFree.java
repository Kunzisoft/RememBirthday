package com.kunzisoft.remembirthday.factory;

import android.content.Context;

/**
 * Menu Factory for Free variant of application
 */
public class MenuFactoryFree extends MenuFactoryBase {

    public MenuFactoryFree(Context context, boolean asPhoneNumber) {
        super(context, asPhoneNumber);
        listMenuAction.add(new MenuActionGift(MenuAction.STATE.NOT_AVAILABLE));
        if(asPhoneNumber) {
            listMenuAction.add(new MenuActionAutoMessage(MenuAction.STATE.NOT_AVAILABLE));
        }
    }
}
