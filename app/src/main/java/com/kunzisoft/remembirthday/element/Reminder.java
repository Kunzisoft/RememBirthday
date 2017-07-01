package com.kunzisoft.remembirthday.element;

import java.util.Date;

/**
 * Created by joker on 01/07/17.
 */

public class Reminder {
    public static final long ID_UNDEFINED = -1;

    protected long id;
    protected Date date;

    /**
     * Create default auto message
     */
    public Reminder() {
        id = ID_UNDEFINED;
        // TODO create delta date
        date = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
