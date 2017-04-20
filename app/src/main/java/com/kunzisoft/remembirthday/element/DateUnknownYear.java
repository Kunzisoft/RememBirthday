package com.kunzisoft.remembirthday.element;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MonthDay;
import org.joda.time.Years;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class for manage a date who can contains a year or not
 */
public class DateUnknownYear implements Parcelable {

    private static String YEAR_FORMAT = "yy-MM-dd";
    private static String WITHOUT_YEAR_FORMAT = "MM-dd";
    private static SimpleDateFormat YEAR_SDF = new SimpleDateFormat(YEAR_FORMAT, Locale.getDefault());
    private static SimpleDateFormat WITHOUT_YEAR_SDF = new SimpleDateFormat(WITHOUT_YEAR_FORMAT, Locale.getDefault());

    private boolean containsYear;
    private Date date;

    /**
     * Construct date with unknown year indication
     * @param date date to store
     * @param containsYear true if year is known, false elsewhere
     */
    public DateUnknownYear(Date date, boolean containsYear) {
        this.containsYear = containsYear;
        this.date = date;
    }

    private DateUnknownYear(Parcel in) {
        this.containsYear = in.readByte() != 0;
        this.date = (Date) in.readSerializable();
    }

    public static DateUnknownYear getDefault() {
        return new DateUnknownYear(new Date(), false);
    }

    public boolean containsYear() {
        return containsYear;
    }

    public void setContainsYear(boolean containsYear) {
        this.containsYear = containsYear;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Return number of days between today and the next date in a year
     * @param date date for calculate delta
     * @return Number of days always >= 0
     */
    public static int daysBetweenTodayAnd(Date date) {
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
        return daysBetweenTodayAnd(this.date);
    }

    //TODO TEST
    public Date getNextAnniversary() {
        MonthDay monthDayNow = MonthDay.now();
        MonthDay monthDayOfNextDate = MonthDay.fromDateFields(date);
        if(monthDayNow.isEqual(monthDayOfNextDate))
            return new DateTime().toDate();
        if(monthDayNow.isBefore(monthDayOfNextDate))
            return new DateTime(date).toDate();
        else {
            DateTime dateTimeNow = DateTime.now();
            DateTime dateTimeOfNextDate = new DateTime(date).withYear(dateTimeNow.getYear()).plusYears(1);
            return dateTimeOfNextDate.toDate();
        }
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

    @Override
    public String toString() {
        if(!containsYear())
            return toStringWithoutYear();
        else
            return toStringWithYear();
    }

    private String toStringWithYear() {
        SimpleDateFormat datePattern = new SimpleDateFormat (YEAR_FORMAT, Locale.getDefault());
        return datePattern.format(date);
    }

    private String toStringWithoutYear() {
        SimpleDateFormat datePattern = new SimpleDateFormat (WITHOUT_YEAR_FORMAT, Locale.getDefault());
        return datePattern.format(date);
    }

    /**
     * Convert string formatted in "MM-dd" to DateUnknownYear
     * @param string
     * @return
     * @throws ParseException
     */
    public static DateUnknownYear stringToDateWithUnknownYear(String string) throws ParseException {
        return new DateUnknownYear(WITHOUT_YEAR_SDF.parse(string), false);
    }

    /**
     * Convert string formatted in "yy-MM-dd" to DateUnknownYear
     * @param string
     * @return
     * @throws ParseException
     */
    public static DateUnknownYear stringToDateWithKnownYear(String string) throws ParseException {
        return new DateUnknownYear(YEAR_SDF.parse(string), true);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (containsYear ? 1 : 0));
        parcel.writeSerializable(date);
    }

    public static final Parcelable.Creator<DateUnknownYear> CREATOR = new Parcelable.Creator<DateUnknownYear>() {
        public DateUnknownYear createFromParcel(Parcel in) {
            return new DateUnknownYear(in);
        }

        public DateUnknownYear[] newArray(int size) {
            return new DateUnknownYear[size];
        }
    };
}
