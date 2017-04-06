package com.kunzisoft.remembirthday.element;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MonthDay;
import org.joda.time.Years;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class for manage a date who can contains a year or not
 */
public class DateUnknownYear {

    private Date date;
    private boolean unknownYear;

    /**
     * Construct date with unknown year indication
     * @param date date to store
     * @param unknownYear true if year is known, false elsewhere
     */
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

    /**
     * Return number of days between today and the next date in a year
     * @param date date for calculate delta
     * @return Number of days always >= 0
     */
    public static int daysInAYearYearBetweenTodayAnd(Date date) {
        MonthDay monthDayNow = MonthDay.now();
        MonthDay monthDayOfNextDate = MonthDay.fromDateFields(date);
        if(monthDayNow.isEqual(monthDayOfNextDate))
            return 0;
        if(monthDayNow.isBefore(monthDayOfNextDate))
            return Days.daysBetween(monthDayNow, monthDayOfNextDate).getDays();
        else {
            DateTime dateTimeNow = DateTime.now();
            DateTime dateTimeOfNextDate = new DateTime(date).withYear(dateTimeNow.getYear()).plusYears(1);
            return Days.daysBetween(DateTime.now(), dateTimeOfNextDate).getDays();
        }
    }

    /**
     * Return number of days between today and the date (this) in a year<br />
     * @return Number of days always >= 0
     */
    public int getDeltaDaysInAYear() {
        return daysInAYearYearBetweenTodayAnd(this.date);
    }

    /**
     * Number of years between a date and today
     * @param date Date: Year for calculate number
     * @return int: Number of days
     */
    public static int yearsBetweenTodayAnd(Date date) {
        return Years.yearsBetween(DateTime.now(), new DateTime(date)).getYears();
    }

    /**
     * Number of years between the date (this) and today
     * @return int: Number of days
     */
    public int getDeltaYears() {
        return yearsBetweenTodayAnd(this.date);
    }

    public String toString() {
        SimpleDateFormat datePattern = new SimpleDateFormat ("yy-MM-dd", Locale.getDefault());
        return datePattern.format(date);
    }
}
