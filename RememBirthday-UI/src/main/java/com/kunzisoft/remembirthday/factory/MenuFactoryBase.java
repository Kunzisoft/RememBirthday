package com.kunzisoft.remembirthday.factory;

import android.content.Context;

import com.kunzisoft.remembirthday.preference.PreferencesManager;

/**
 * Menu Factory for base variant of application
 */
public abstract class MenuFactoryBase extends MenuContact {

    public MenuFactoryBase(Context context, boolean asPhoneNumber) {
        super();
        listMenuAction.add(new MenuActionCalendar());
        if(asPhoneNumber) {
            listMenuAction.add(new MenuActionMessage());
            listMenuAction.add(new MenuActionCall());
        }
        if(PreferencesManager.isCustomCalendarActive(context))
            listMenuAction.add(new MenuActionReminder());
        else
            listMenuAction.add(new MenuActionReminder(MenuAction.STATE.INACTIVE));
    }
}
