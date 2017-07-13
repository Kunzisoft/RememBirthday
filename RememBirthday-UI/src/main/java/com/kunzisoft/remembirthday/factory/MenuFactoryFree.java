package com.kunzisoft.remembirthday.factory;

/**
 * Created by joker on 13/07/17.
 */

public class MenuFactoryFree extends MenuFactory {

    public MenuFactoryFree() {
        super();
        MenuAction menuCalendar = new MenuActionCalendar();
        MenuAction menuReminder = new MenuActionReminder(false);
        MenuAction menuAutoMessage = new MenuActionAutoMessage(false);
        MenuAction menuMessage = new MenuActionMessage();

        listMenuAction.add(menuCalendar);
        listMenuAction.add(menuReminder);
        listMenuAction.add(menuAutoMessage);
        listMenuAction.add(menuMessage);
    }

    public void setAction(ActionContactMenu actionContactMenu) {

    }
}
