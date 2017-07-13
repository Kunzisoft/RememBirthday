package com.kunzisoft.remembirthday.factory;

/**
 * Created by joker on 13/07/17.
 */

public class MenuFactoryLibre extends MenuFactory {

    public MenuFactoryLibre() {
        super();
        MenuAction menuCalendar = new MenuActionCalendar();
        listMenuAction.add(menuCalendar);
    }
}
