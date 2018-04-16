package com.kunzisoft.remembirthday.factory;

import android.content.Context;

import com.kunzisoft.remembirthday.BuildConfig;

/**
 * Created by joker on 15/07/17.
 */

public class MenuContactCreator {

    private MenuContact menuFactory;

    public static MenuContact emptyMenuContact() {
        return new MenuContact() {
            @Override
            public void setActionContactMenu(ActionContactMenu actionContactMenu) {
                super.setActionContactMenu(actionContactMenu);
            }
        };
    }

    public MenuContactCreator(Context context, boolean asPhoneNumber) {
        // List for menu, depend of variant of app
        if(!BuildConfig.FULL_VERSION)
            menuFactory = new MenuFactoryFree(context, asPhoneNumber);
        else {
            menuFactory = new MenuFactoryPro(context, asPhoneNumber);
        }
    }

    public MenuContact create() {
        return menuFactory;
    }
}
