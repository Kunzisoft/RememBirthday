package com.kunzisoft.remembirthday.element;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.preference.PreferencesHelper;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.sufficientlysecure.htmltextview.HtmlTextView.TAG;

/**
 * Manage Event of Calendar, all dates are in LocalTimeZone
 * Created by joker on 27/07/17.
 */
public class CalendarEvent implements Parcelable {

    public static final long ID_UNDEFINED = -1;

    private long id;
    private Date dateStart;
    private Date dateStop;
    private boolean allDay;
    private String title;
    private String description;
    private List<Reminder> reminders;

    public static String getEventTitleFromContact(Context context, Contact contact) {
        if (contact.hasBirthday()) {
            if(!contact.getBirthday().containsYear())
                return context.getString(R.string.event_title_without_year, contact.getName());
            else
                return context.getString(R.string.event_title, contact.getName(), contact.getAgeToNextBirthday());
        }
        return "";
    }

    public static CalendarEvent buildDefaultEventFromContactToSave(Context context, Contact contact) {
        CalendarEvent event = new CalendarEvent(getEventTitleFromContact(context, contact),
                contact.getNextBirthday(), true);
        int[] defaultTime = PreferencesHelper.getDefaultTime(context);
        event.addReminder(
                new Reminder(event.getDate(), defaultTime[0], defaultTime[1]));

        Log.e(TAG, event.toString());
        return event;
    }

    public CalendarEvent(String title, Date date) {
        this(title, date, date);
    }

    public CalendarEvent(String title, Date date, boolean allDay) {
        this(title, date, date);
        setAllDay(allDay);
    }

    public CalendarEvent(String title, Date dateStart, Date dateStop) {
        this.id = ID_UNDEFINED;
        this.dateStart = dateStart;
        this.dateStop = dateStop;
        this.allDay = false;
        this.title = title;
        this.reminders = new ArrayList<>();
    }

    /**
     * Create a copy of event
     * @param another Base event
     */
    public CalendarEvent(CalendarEvent another) {
        this.id = ID_UNDEFINED;
        this.dateStart = another.dateStart;
        this.dateStop = another.dateStop;
        setAllDay(another.allDay);
        this.title = another.title;
        this.reminders = new ArrayList<>();
        for(Reminder reminder : another.reminders) {
            reminders.add(new Reminder(reminder));
        }
    }

    private CalendarEvent(Parcel in) {
        id = in.readLong();
        dateStart = (Date) in.readSerializable();
        dateStop = (Date) in.readSerializable();
        allDay = in.readByte() != 0;
        title = in.readString();
        reminders = in.readArrayList(Reminder.class.getClassLoader());
    }

    public boolean hasId() {
        return id != ID_UNDEFINED;
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
        return new DateTime(getDate()).getYear();
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
        if(allDay) {
            dateStart = new DateTime(dateStart)
                    .withHourOfDay(0)
                    .withMinuteOfHour(0)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0)
                    .toDate();
            dateStop = new DateTime(dateStart)
                    .plusDays(1)
                    .toDate();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addReminder(Reminder reminder) {
        this.reminders.add(reminder);
    }

    public void addReminders(List<Reminder> reminders) {
        this.reminders.addAll(reminders);
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
                "id=" + id +
                ", dateStart=" + dateStart +
                ", dateStop=" + dateStop +
                ", allDay=" + allDay +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", reminders=" + reminders +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarEvent that = (CalendarEvent) o;

        if (allDay != that.allDay) return false;
        if (dateStart != null ? !dateStart.equals(that.dateStart) : that.dateStart != null)
            return false;
        if (dateStop != null ? !dateStop.equals(that.dateStop) : that.dateStop != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = dateStart != null ? dateStart.hashCode() : 0;
        result = 31 * result + (dateStop != null ? dateStop.hashCode() : 0);
        result = 31 * result + (allDay ? 1 : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
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
