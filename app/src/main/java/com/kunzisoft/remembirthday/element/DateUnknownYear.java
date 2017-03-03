package com.kunzisoft.remembirthday.element;

import java.util.Date;

/**
 * Created by joker on 02/03/17.
 */
public class DateUnknownYear {

    private Date date;
    private boolean unknownYear;

    public DateUnknownYear(Date date, boolean unknownYear) {
        this.date = date;
        this.unknownYear = unknownYear;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isUnknownYear() {
        return unknownYear;
    }

    public void setUnknownYear(boolean unknownYear) {
        this.unknownYear = unknownYear;
    }
}
