package com.kunzisoft.remembirthday.element;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by joker on 27/07/17.
 */

public class CalendarEvent implements Parcelable {

    private long id;
    private Date dateStart;
    private Date dateStop;
    private boolean allDay;
    private String title;
    private List<Reminder> reminders;


    public CalendarEvent(Date date) {
        this(date, date);
    }

    public CalendarEvent(Date dateStart, Date dateStop) {
        this.id = -1;
        this.dateStart = dateStart;
        this.dateStop = dateStop;
        this.allDay = false;
        this.title = "";
        this.reminders = new ArrayList<>();
    }

    private CalendarEvent(Parcel in) {
        id = in.readLong();
        dateStart = (Date) in.readSerializable();
        dateStop = (Date) in.readSerializable();
        allDay = in.readByte() != 0;
        title = in.readString();
        reminders = in.readArrayList(Reminder.class.getClassLoader());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return dateStart;
    }

    /**
     * Get the year from date of event
     * @return
     */
    public int getYear() {
        return new DateTime(getDate().getDate()).getYear();
    }

    /**
     * Set the year to the date of event
     */
    public void setYear(int year) {
        int secondsBetweenStartAndStop = Seconds.secondsBetween(
                new DateTime(dateStart),
                new DateTime(dateStop))
                .getSeconds();
        dateStart = new DateTime(dateStart).withYear(year).toDate();
        dateStop = new DateTime(dateStart).plusSeconds(secondsBetweenStartAndStop).toDate();
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateStop() {
        return dateStop;
    }

    public void setDateStop(Date dateStop) {
        this.dateStop = dateStop;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;

        Calendar cal = Calendar.getInstance();
        cal.setTime(getDate());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        /*
         * Allday events have to be set in UTC!
         *
         * Without UTC it results in: CalendarProvider2 W insertInTransaction: allDay is true but
         * sec, min, hour were not 0.
         * http://stackoverflow.com/questions/3440172/getting-exception-when
         * -inserting-events-in-android-calendar
         */
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        /*
         * Define over entire day.
         *
         * Note: ALL_DAY is enough on original Android calendar, but some calendar apps (Business
         * Calendar) do not display the event if time between dtstart and dtend is 0
         */
        long startMilliseconds = cal.getTimeInMillis();
        long endMilliseconds = startMilliseconds + DateUtils.DAY_IN_MILLIS;

        dateStart = new Date(startMilliseconds);
        dateStop = new Date(endMilliseconds);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void deleteAllReminders() {
        reminders.clear();
    }

    @Override
    public String toString() {
        return "CalendarEvent{" +
                "dateStart=" + dateStart +
                ", dateStop=" + dateStop +
                ", allDay=" + allDay +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeSerializable(dateStart);
        parcel.writeSerializable(dateStop);
        parcel.writeByte((byte) (allDay ? 1 : 0));
        parcel.writeString(title);
        parcel.writeList(reminders);
    }

    public static final Parcelable.Creator<CalendarEvent> CREATOR = new Parcelable.Creator<CalendarEvent>() {
        public CalendarEvent createFromParcel(Parcel in) {
            return new CalendarEvent(in);
        }

        public CalendarEvent[] newArray(int size) {
            return new CalendarEvent[size];
        }
    };
}
