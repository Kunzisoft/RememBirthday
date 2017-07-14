package com.kunzisoft.remembirthday.factory;

/**
 * Menu Factory for Free variant of application
 */
public class MenuFactoryFree extends MenuFactory {

    public MenuFactoryFree() {
        super();
        listMenuAction.add(new MenuActionCalendar());
        listMenuAction.add(new MenuActionMessage());
        listMenuAction.add(new MenuActionCall());
        listMenuAction.add(new MenuActionReminder(false));
        listMenuAction.add(new MenuActionAutoMessage(false));
        listMenuAction.add(new MenuActionAutoVoice(false));
    }
}
