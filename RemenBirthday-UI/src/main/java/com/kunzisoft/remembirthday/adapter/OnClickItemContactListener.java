package com.kunzisoft.remembirthday.adapter;

import android.database.Cursor;
import android.view.View;

import com.kunzisoft.remembirthday.element.Contact;

/**
 * Listener when a click on contact item is performed
 */
public interface OnClickItemContactListener {
    /**
     * Callback method called when a click event on item is performed
     * @param view The view clicked
     * @param contact The contact build with information get in database
     * @param cursor The cursor used to get data
     * @param position The position of item, can be used for retrieve data with cursor
     */
    void onItemContactClick(View view, Contact contact, Cursor cursor, int position);
}
