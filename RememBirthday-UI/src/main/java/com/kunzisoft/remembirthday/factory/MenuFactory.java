package com.kunzisoft.remembirthday.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 13/07/17.
 */

public abstract class MenuFactory {

    protected List<MenuAction> listMenuAction;

    protected MenuFactory() {
        listMenuAction = new ArrayList<>();
    }

    public List<MenuAction> createMenu() {
        return listMenuAction;
    }

    public MenuAction getMenu(int position) {
        try {
            return listMenuAction.get(position);
        } catch(IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getMenuCount() {
        return listMenuAction.size();
    }
}
