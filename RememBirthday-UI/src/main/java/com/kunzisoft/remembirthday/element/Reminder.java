package com.kunzisoft.remembirthday.element;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;

import java.util.Date;

/**
 * Created by joker on 01/07/17.
 */
public class Reminder implements Parcelable{
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
                .withMillisOfSecond(0)
                .toDate();
        this.hourOfDay = hourOfDay;
        this.minuteOfHour = minuteOfHour;
        this.daysBefore = deltaDay;
    }

    /**
     * Create default auto message for date of anniversary
     */
    public Reminder(Date dateEvent, int hourOfDay, int minuteOfHour) {
        this(dateEvent, hourOfDay, minuteOfHour, 0);
    }

    /**
     * Create default auto message for date of anniversary
     */
    public Reminder(Date dateEvent, int minuteBeforeEvent) {
        this.id = ID_UNDEFINED;
        this.dateEvent = dateEvent;
        this.dateEvent = new DateTime(this.dateEvent)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)
                .toDate();
        DateTime dateReminder = new DateTime(dateEvent).minusMinutes(minuteBeforeEvent);
        this.hourOfDay = dateReminder.getHourOfDay();
        this.minuteOfHour = dateReminder.getMinuteOfHour();
        this.daysBefore = Days.daysBetween(dateReminder, new DateTime(dateEvent)).getDays();
        if(minuteBeforeEvent > 0)
            this.daysBefore++;
    }

    /**
     * Create a copy of Reminder
     * @param another
     */
    public Reminder(Reminder another) {
        this.id = ID_UNDEFINED;
        this.dateEvent = another.dateEvent;
        this.hourOfDay = another.hourOfDay;
        this.minuteOfHour = another.minuteOfHour;
        this.daysBefore = another.daysBefore;
    }

    public Reminder(Parcel in) {
        this.id = in.readLong();
        this.dateEvent = new Date(in.readLong());
        this.hourOfDay = in.readInt();
        this.minuteOfHour = in.readInt();
        this.daysBefore = in.readInt();
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

    public void setDateEvent(Date dateEvent) {
        this.dateEvent = dateEvent;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(dateEvent.getTime());
        parcel.writeInt(hourOfDay);
        parcel.writeInt(minuteOfHour);
        parcel.writeInt(daysBefore);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };
}
