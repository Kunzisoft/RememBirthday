package com.kunzisoft.remembirthday.activity;

import com.kunzisoft.remembirthday.element.Contact;

/**
 * Created by joker on 24/04/17.
 */

public interface AnniversaryDialogOpen {

    /**
     * Displays the event selection dialog
     * @param contact Contact who contains and RowId of contact defined in ContactsContract.Data.RAW_CONTACT_ID
     */
    void openAnniversaryDialogSelection(Contact contact);
}
