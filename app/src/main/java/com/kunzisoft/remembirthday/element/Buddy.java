package com.kunzisoft.remembirthday.element;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by joker on 15/12/16.
 */
public class Buddy {

    private String name;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Return number of days between today and the birthday
     * @param birthday Birthday of buddy
     * @return Number of days
     */
    public static int getStayDays(Date birthday) {

        Calendar calendar = Calendar.getInstance();
        Date today = new Date();

        // Todo difference date
        return 12;
    }
}
