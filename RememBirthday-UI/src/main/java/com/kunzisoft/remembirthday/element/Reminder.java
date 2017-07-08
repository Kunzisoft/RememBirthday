package com.kunzisoft.remembirthday.element;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by joker on 01/07/17.
 */

public class Reminder {
    public static final long ID_UNDEFINED = -1;

    private long id;
    protected Date anniversary;
    protected int hourOfDay;
    protected int minuteOfHour;
    protected int daysBefore;

    /**
     * Create default auto message
     */
    public Reminder(Date anniversary, int hourOfDay, int minuteOfHour, int deltaDay) {
        this.id = ID_UNDEFINED;
        this.anniversary = anniversary;
        this.anniversary = new DateTime(this.anniversary)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .toDate();
        this.hourOfDay = hourOfDay;
        this.minuteOfHour = minuteOfHour;
        this.daysBefore = deltaDay;
    }

    /**
     * Create default auto message for date of anniversary
     */
    public Reminder(Date anniversary, int hourOfDay, int minuteOfHour) {
        this(anniversary, hourOfDay, minuteOfHour, 0);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return new DateTime(anniversary)
                .withMinuteOfHour(minuteOfHour)
                .withHourOfDay(hourOfDay)
                .minusDays(daysBefore)
                .toDate();
    }

    public int getDeltaDay() {
        return daysBefore;
    }

    public void setDeltaDay(int deltaDay) {
        this.daysBefore = deltaDay;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hour) {
        this.hourOfDay = hour;
    }

    public int getMinuteOfHour() {
        return minuteOfHour;
    }

    public void setMinuteOfHour(int minute) {
        this.minuteOfHour = minute;
    }
}
