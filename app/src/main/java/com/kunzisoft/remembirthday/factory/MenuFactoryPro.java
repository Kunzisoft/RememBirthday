package com.kunzisoft.remembirthday.factory;

import android.content.Context;

import com.kunzisoft.remembirthday.preference.PreferencesManager;

/**
 * Menu Factory for Pro version of application. <br />
 * Automatically hides the daemon-dependent buttons when they are inactive
 */
public class MenuFactoryPro extends MenuFactoryBase {

    public MenuFactoryPro(Context context, boolean asPhoneNumber) {
        super(context, asPhoneNumber);
        if(PreferencesManager.isDaemonsActive(context)) {
            listMenuAction.add(new MenuActionGift());
            if(asPhoneNumber) {
                listMenuAction.add(new MenuActionAutoMessage());
            }
        } else {
            if(!PreferencesManager.isButtonsForInactiveFeaturesHidden(context)) {
                listMenuAction.add(new MenuActionGift(MenuAction.STATE.INACTIVE));
                if (asPhoneNumber) {
                    listMenuAction.add(new MenuActionAutoMessage(MenuAction.STATE.INACTIVE));
                }
            }
        }
    }
}
