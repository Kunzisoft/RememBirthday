package com.kunzisoft.remembirthday.factory;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.kunzisoft.remembirthday.Utility;
import com.kunzisoft.remembirthday.element.Contact;

/**
 * Created by joker on 13/07/17.
 */

public abstract class MenuAction {

    private int titleId;
    private int imageId;
    private boolean active;

    public MenuAction(int titleId, int imageId) {
        this(titleId, imageId, true);
    }

    public MenuAction(int titleId, int imageId, boolean active) {
        this.titleId = titleId;
        this.imageId = imageId;
        this.active = active;
    }

    public int getTitleId() {
        return titleId;
    }

    public int getImageId() {
        return imageId;
    }

    public boolean isActive() {
        return active;
    }

    public void doAction(Context context,
                         FragmentManager fragmentManager,
                         Contact contact) {
        if(!isActive()) {
            Utility.openProFeatureDialog(fragmentManager);
        }
    }
}
