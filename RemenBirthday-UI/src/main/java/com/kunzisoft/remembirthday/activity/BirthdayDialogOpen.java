package com.kunzisoft.remembirthday.activity;

/**
 * Created by joker on 24/04/17.
 */

public interface BirthdayDialogOpen {

    /**
     * Displays the event selection dialog
     * @param rawContactId Id of contact defined in ContactsContract.Data.RAW_CONTACT_ID
     */
    void openDialogSelection(long rawContactId);
}
