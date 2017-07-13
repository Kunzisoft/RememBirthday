package com.kunzisoft.remembirthday.factory;

import android.content.Context;

import com.kunzisoft.remembirthday.preference.PreferencesManager;

/**
 * Menu Factory for Pro version of application. <br />
 * Automatically hides the daemon-dependent buttons when they are inactive
 */
public class MenuFactoryPro extends MenuFactory {

    public MenuFactoryPro(Context context) {
        super();
        listMenuAction.add(new MenuActionCalendar());
        listMenuAction.add(new MenuActionMessage());
        if(PreferencesManager.isDaemonsActive(context)) {
            listMenuAction.add(new MenuActionReminder());
            listMenuAction.add(new MenuActionAutoMessage());
        }
    }
}
