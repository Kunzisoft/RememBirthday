package com.kunzisoft.remembirthday.element;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MonthDay;
import org.joda.time.Years;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Class for manage a date who can contains a year or not
 */
public class DateUnknownYear implements Parcelable {

    private static final String TAG = "DateUnknownYear";

    private static String WITH_YEAR_FORMAT_DEFAULT = "yyyy-MM-dd";
    private static String WITHOUT_YEAR_FORMAT_DEFAULT = "--MM-dd";
    private static int YEAR_UNKNOWN_DEFAULT = 1700;

    private boolean containsYear;
    private Date date;

    /**
     * Construct date with unknown year indication, contains year by default
     * @param date date to store
     */
    public DateUnknownYear(Date date) {
        this.containsYear = true;
        setDate(date);
    }

    /**
     * Construct date with unknown year indication
     * @param date date to store
     * @param containsYear true if year is known, false elsewhere
     */
    public DateUnknownYear(Date date, boolean containsYear) {
        this.containsYear = containsYear;
        setDate(date);
    }

    private DateUnknownYear(Parcel in) {
        this.containsYear = in.readByte() != 0;
        setDate((Date) in.readSerializable());
    }

    /**
     * @return Return the default unknown date
     */
    public static DateUnknownYear getDefault() {
        return new DateUnknownYear(new Date(0), false);
    }

    /**
     * Determines whether the year is present in the date
     * @return true is is present
     */
    public boolean containsYear() {
        return containsYear;
    }

    /**
     * Determines whether the year is present or not.
     * @param containsYear true if the year is present
     */
    public void setContainsYear(boolean containsYear) {
        this.containsYear = containsYear;
    }

    /**
     * @return The corresponding date, <br />
     * Caution: Hours, minutes and seconds are random, the year is random if not determined.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Get the date with a specific year
     * @param year Year
     * @return Date with year
     */
    public Date getDateWithYear(int year) {
        return getDateWithYear(this.date, year);
    }

    /**
     * Get the date with a specific year
     * @param date Date
     * @param year Year
     * @return Date with year
     */
    public static Date getDateWithYear(Date date, int year) {
        return new DateTime(date).withYear(year).toDate();
    }

    /**
     * Assign a new date, only day, month and year are used.
     * @param date The new date
     */
    public void setDate(Date date) {
        this.date = date;
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        if(year == YEAR_UNKNOWN_DEFAULT)
            setContainsYear(false);
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

    /**
     * If next Anniversary of date is today
     * @return True if it's the anniversary
     */
    public boolean nextAnniversaryIsToday() {
        return getDeltaDaysInAYear() == 0;
    }

    /**
     * Gets the anniversary not yet passed
     * @return Next anniversary in the year
     */
    // TODO TEST
    public Date getNextAnniversary() {
        return getNextAnniversary(this.date);
    }

    /**
     * Gets the anniversary not yet passed without hour, minute, second and millisecond
     * @return Next anniversary in the year
     */
    // TODO TEST
    public Date getNextAnniversaryWithoutHour() {
        DateTime dateWithoutHour = new DateTime(getNextAnniversary(this.date));
        dateWithoutHour = dateWithoutHour.withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        return dateWithoutHour.toDate();
    }

    /**
     * Gets the anniversary date not yet passed
     * @param date Date without year
     * @return Next anniversary in the year
     */
    public static Date getNextAnniversary(DateUnknownYear date) {
        return getNextAnniversary(date.getDate());
    }

    /**
     * Gets the anniversary date not yet passed (with delta of 1 day)
     * @param date Date
     * @return Next anniversary in the year
     */
    public static Date getNextAnniversary(Date date) {
        DateTime dateTimeNow = DateTime.now();
        MonthDay monthDayNow = MonthDay.now();
        MonthDay monthDayOfNextDate = MonthDay.fromDateFields(date);
        if(monthDayNow.isEqual(monthDayOfNextDate)) {
            DateTime inputDate = new DateTime(date);
            return dateTimeNow
                    .withHourOfDay(inputDate.getHourOfDay())
                    .withMinuteOfHour(inputDate.getMinuteOfHour())
                    .withSecondOfMinute(inputDate.getSecondOfMinute())
                    .withMillisOfSecond(inputDate.getMillisOfSecond()).toDate();
        } if(monthDayNow.isBefore(monthDayOfNextDate))
            return new DateTime(date).withYear(dateTimeNow.getYear()).toDate();
        else {
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

    /**
     * String for backup in a database
     * @return The string for backup
     */
    public String toBackupString() {
        if (!containsYear()){
            SimpleDateFormat datePattern = new SimpleDateFormat(WITHOUT_YEAR_FORMAT_DEFAULT, Locale.getDefault());
            return datePattern.format(date);
        } else {
            SimpleDateFormat datePattern = new SimpleDateFormat(WITH_YEAR_FORMAT_DEFAULT, Locale.getDefault());
            return datePattern.format(date);
        }
    }

    @Override
    public String toString() {
        int dateFormat = DateFormat.MEDIUM;
        if(containsYear()) {
            DateFormat df = DateFormat.getDateInstance(dateFormat, Locale.getDefault());
            return df.format(date);
        } else {
            return toStringMonthAndDay(dateFormat);
        }
    }

    /**
     * @return Formatted string only see the year, must be used with {@link DateUnknownYear#containsYear()} <br />
     * WARNING : If the date does not contain a year, returns a random number.
     */
    public String toStringYear() {
        SimpleDateFormat yearSimpleDateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        return yearSimpleDateFormat.format(date);
    }

    /**
     * Formatted string only see the month and the day
     * @param dateFormat can be {@link DateFormat#SHORT}, {@link DateFormat#MEDIUM} or {@link DateFormat#LONG}
     * @return Formatted string
     */
    public String toStringMonthAndDay(int dateFormat) {
        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(dateFormat);
        sdf.applyPattern(sdf.toPattern().replaceAll(
                "([^\\p{Alpha}']|('[\\p{Alpha}]+'))*y+([^\\p{Alpha}']|('[\\p{Alpha}]+'))*",
                ""));
        return sdf.format(date);
    }

    /**
     * Try to parse input with SimpleDateFormat
     *
     * @param format SimpleDateFormat
     * @param withYear When true the age will be not displayed in brackets
     * @return Date object if successful, otherwise null
     */
    private static DateUnknownYear parseStringWithSimpleDateFormat(String input, String format,
                                                        boolean withYear) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        try {
            DateUnknownYear dateUnknownYear = DateUnknownYear.getDefault();
            Date parsedDate = dateFormat.parse(input);
            dateUnknownYear.setContainsYear(withYear);
            dateUnknownYear.setDate(parsedDate);
            return dateUnknownYear;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * The date format in the contact events is not standardized! This method will try to parse it
     * trying different date formats.
     * <p/>
     * See also: http://dmfs.org/carddav/?date_format
     *
     * @return eventDate as Date object
     */
    public static DateUnknownYear stringToDate(String eventDateString) {
        DateUnknownYear date;
        if (eventDateString != null) {
            // yyyy-MM-dd, Most used format!
            date = parseStringWithSimpleDateFormat(eventDateString, "yy-MM-dd", true);
            if (date == null) {
                date = parseStringWithSimpleDateFormat(eventDateString, "yyyy-MM-dd", false);
            }
            // --MM-dd, Most used format without year!
            if (date == null) {
                date = parseStringWithSimpleDateFormat(eventDateString, "--MM-dd", false);
            }
            if (date == null) {
                date = parseStringWithSimpleDateFormat(eventDateString, "MM-dd", false);
            }
            // yyyyMMdd, HTC Desire
            if (date == null) {
                if (eventDateString.length() == 8) {
                    date = parseStringWithSimpleDateFormat(eventDateString, "yyyyMMdd", true);
                }
            }
            // Unix timestamp, Some Motorola devices
            if (date == null) {
                Log.d(TAG, "Trying to parse Event Date String " + eventDateString
                        + " as a unix timestamp!");
                try {
                    Date rawDate = new Date(Long.parseLong(eventDateString));
                } catch (NumberFormatException e) {
                    Log.d(TAG, "Parsing failed!");
                }
            }
            // dd.MM.yyyy
            if (date == null) {
                date = parseStringWithSimpleDateFormat(eventDateString, "dd.MM.yyyy", true);
            }
            // yyyy.MM.dd
            if (date == null) {
                date = parseStringWithSimpleDateFormat(eventDateString, "yyyy.MM.dd", true);
            }
            // MM/dd/yyyy, Used by Facebook
            if (date == null) {
                date = parseStringWithSimpleDateFormat(eventDateString, "MM/dd/yyyy", true);
            }
            //MM/dd, Used by Facebook
            if (date == null) {
                date = parseStringWithSimpleDateFormat(eventDateString, "MM/dd", false);
            }
            /* Return */
            if (date != null) {
                Log.d(TAG, "Event Date String " + eventDateString + " was parsed as "
                        + date.toString());
                return date;
            } else {
                Log.e(TAG, "Event Date String " + eventDateString
                        + " could NOT be parsed! returning null!");
                return null;
            }
        } else {
            Log.d(TAG, "Event Date String is null!");
            return null;
        }
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
