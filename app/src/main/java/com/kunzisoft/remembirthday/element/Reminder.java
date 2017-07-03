package com.kunzisoft.remembirthday.element;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by joker on 01/07/17.
 */

public class Reminder {
    public static final long ID_UNDEFINED = -1;

    public static final int DEFAULT_HOUR = 10;
    public static final int DEFAULT_MINUTE = 0;

    protected long id;
    protected Date date;

    /**
     * Create default auto message
     */
    public Reminder(Date anniversary) {
        id = ID_UNDEFINED;
        // TODO create delta date
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(anniversary);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, DEFAULT_MINUTE);
        calendar.set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR);
        date = calendar.getTime();
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
