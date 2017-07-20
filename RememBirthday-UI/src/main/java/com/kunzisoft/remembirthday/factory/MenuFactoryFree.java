package com.kunzisoft.remembirthday.factory;

/**
 * Menu Factory for Free variant of application
 */
public class MenuFactoryFree extends MenuFactoryBase {

    public MenuFactoryFree(boolean asPhoneNumber) {
        super(asPhoneNumber);
        listMenuAction.add(new MenuActionReminder(MenuAction.STATE.INACTIVE_FOR_PRO));
        listMenuAction.add(new MenuActionGift(MenuAction.STATE.INACTIVE_FOR_PRO));
        if(asPhoneNumber) {
            listMenuAction.add(new MenuActionAutoMessage(MenuAction.STATE.INACTIVE_FOR_PRO));
            //listMenuAction.add(new MenuActionAutoVoice(MenuAction.STATE.INACTIVE_FOR_PRO));
        }
    }
}
