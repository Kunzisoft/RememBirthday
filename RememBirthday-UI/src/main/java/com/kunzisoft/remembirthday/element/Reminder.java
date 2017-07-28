package com.kunzisoft.remembirthday.element;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.Date;

/**
 * Created by joker on 01/07/17.
 */

public class Reminder {
    public static final long ID_UNDEFINED = -1;

    private long id;
    protected Date dateEvent;
    protected int hourOfDay;
    protected int minuteOfHour;
    protected int daysBefore;

    /**
     * Create default auto message
     */
    public Reminder(Date dateEvent, int hourOfDay, int minuteOfHour, int deltaDay) {
        this.id = ID_UNDEFINED;
        this.dateEvent = dateEvent;
        this.dateEvent = new DateTime(this.dateEvent)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
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

    /**
     * Create a copy of Reminder
     * @param another
     */
    public Reminder(Reminder another) {
        this.id = ID_UNDEFINED;
        this.dateEvent = another.dateEvent;
        this.dateEvent = another.dateEvent;
        this.hourOfDay = another.hourOfDay;
        this.minuteOfHour = another.minuteOfHour;
        this.daysBefore = another.daysBefore;
    }

    public boolean hasId() {
        return id != ID_UNDEFINED;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return new DateTime(dateEvent)
                .withMinuteOfHour(minuteOfHour)
                .withHourOfDay(hourOfDay)
                .minusDays(daysBefore)
                .toDate();
    }

    public int getMinutesBeforeEvent() {
        return Minutes.minutesBetween(new DateTime(getDate()), new DateTime(dateEvent)).getMinutes();
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

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", date=" + getDate() +
                '}';
    }
}
