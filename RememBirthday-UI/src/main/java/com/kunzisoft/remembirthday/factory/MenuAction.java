package com.kunzisoft.remembirthday.factory;

/**
 * Created by joker on 13/07/17.
 */

public abstract class MenuAction {

    private int titleId;
    private int imageId;
    private STATE state;

    private ActionContactMenu actionContactMenu;

    public MenuAction(int titleId, int imageId) {
        this(titleId, imageId, STATE.ACTIVE);
    }

    public MenuAction(int titleId, int imageId, STATE state) {
        this.titleId = titleId;
        this.imageId = imageId;
        this.state = state;
    }

    public abstract int getItemId();

    public int getTitleId() {
        return titleId;
    }

    public int getImageId() {
        return imageId;
    }

    public STATE getState() {
        return state;
    }

    public void setActionContactMenu(ActionContactMenu actionContactMenu) {
        this.actionContactMenu = actionContactMenu;
    }

    public void doAction(MenuAction menuAction, int position) {
        if(actionContactMenu != null)
            actionContactMenu.doActionMenu(menuAction);
    }

    public enum STATE {
        ACTIVE,
        INACTIVE,
        NOT_AVAILABLE
    }
}
