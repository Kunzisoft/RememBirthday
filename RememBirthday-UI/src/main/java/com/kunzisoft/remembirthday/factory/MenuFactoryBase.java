package com.kunzisoft.remembirthday.factory;

/**
 * Menu Factory for base variant of application
 */
public abstract class MenuFactoryBase extends MenuContact {

    public MenuFactoryBase(boolean asPhoneNumber) {
        super();
        listMenuAction.add(new MenuActionCalendar());
        if(asPhoneNumber) {
            listMenuAction.add(new MenuActionMessage());
            listMenuAction.add(new MenuActionCall());
        }
    }
}
