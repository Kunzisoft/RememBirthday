package com.kunzisoft.remembirthday.factory;

/**
 * Created by joker on 13/07/17.
 */

public class MenuFactoryPro extends MenuFactory {

    public MenuFactoryPro() {
        super();
        MenuAction menuCalendar = new MenuActionCalendar();
        MenuAction menuReminder = new MenuActionReminder();
        MenuAction menuAutoMessage = new MenuActionAutoMessage();
        MenuAction menuMessage = new MenuActionMessage();

        listMenuAction.add(menuCalendar);
        listMenuAction.add(menuReminder);
        listMenuAction.add(menuAutoMessage);
        listMenuAction.add(menuMessage);
    }
}
