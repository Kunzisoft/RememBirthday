package com.kunzisoft.remembirthday.factory;

/**
 * Created by joker on 13/07/17.
 */

public abstract class MenuAction {

    private int titleId;
    private int imageId;
    private boolean active;

    private ActionContactMenu actionContactMenu;

    public MenuAction(int titleId, int imageId) {
        this(titleId, imageId, true);
    }

    public MenuAction(int titleId, int imageId, boolean active) {
        this.titleId = titleId;
        this.imageId = imageId;
        this.active = active;
    }

    public abstract int getItemId();

    public int getTitleId() {
        return titleId;
    }

    public int getImageId() {
        return imageId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActionContactMenu(ActionContactMenu actionContactMenu) {
        this.actionContactMenu = actionContactMenu;
    }

    public void doAction(MenuAction menuAction, int position) {
        if(actionContactMenu != null)
            actionContactMenu.doActionMenu(menuAction);
    }
}
