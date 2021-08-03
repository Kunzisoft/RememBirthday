package com.kunzisoft.remembirthday.preference;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.factory.ContactSort;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to retrieve items from preferences
 * @author joker on 05/07/17.
 */
public class PreferencesHelper {

    public static final String PATTERN_REMINDER_PREF = "^\\d{1,2}+(-\\d{1,2})*$";
    public static final String PATTERN_REMINDER_SPLITTER = "-";

    /**
     * Return true if custom calendar is active, false elsewhere
     * @param context Context to call
     * @return true id active
     */
    public static boolean isCustomCalendarActive(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //TODO false for free and new
        // true for free and old
        // true for pro and old
        // true for pro and new with dialog for duplication
        return prefs.getBoolean(context.getString(R.string.pref_create_calendar_key),
                Boolean.getBoolean(context.getString(R.string.pref_create_calendar_default)));
    }

    /**
     * Return the color of custom calendar
     * @param context Context to call
     * @return Color
     */
    public static @ColorInt
    int getCustomCalendarColor(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(context.getString(R.string.pref_calendar_color_key),
                ContextCompat.getColor(context, R.color.pref_calendar_color_default));
    }

    /**
     * Get default days in preferences
     * @param context Context to call
     * @return Array of days
     */
    public static int[] getDefaultDays(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prefNotificationsDay = prefs.getString(context.getString(R.string.pref_reminders_days_key),
                context.getString(R.string.pref_reminders_days_default));
        Pattern p = Pattern.compile(PATTERN_REMINDER_PREF);
        int[] days;
        Matcher m = p.matcher(prefNotificationsDay);
        if(m.matches()) {
            String[] splitString = prefNotificationsDay.split(PATTERN_REMINDER_SPLITTER);
            days = new int[splitString.length];
            for(int i=0; i<splitString.length; i++) {
                days[i] = Integer.parseInt(splitString[i]);
            }
            return days;
        }
        // 0 if preference can't match
        days = new int[1];
        days[0] = 0;
        return days;
    }

    /**
     * Get default days in preferences as a string
     * @param context Context to call
     * @return String for Array of days with "-" separator
     */
    public static String getDefaultDaysAsString(Context context) {
        StringBuilder stringDays = new StringBuilder();
        boolean firstTime = true;
        for (int day : getDefaultDays(context)) {
            if (firstTime)
                stringDays.append(day);
            else
                stringDays.append(PATTERN_REMINDER_SPLITTER).append(day);
            firstTime = false;
        }
        return stringDays.toString();
    }

    /**
     * Get default time in preferences
     * @param context Context to call
     * @return Array of 2 elements, [0] for hours, [1] for minutes
     */
    public static int[] getDefaultTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prefNotificationsTime = prefs.getString(context.getString(R.string.pref_reminders_time_key),
                context.getString(R.string.pref_reminders_time_default));
        int[] time = new int[2];
        time[0] = TimePreference.parseHour(prefNotificationsTime);
        time[1] = TimePreference.parseMinute(prefNotificationsTime);
        return time;
    }

    /**
     * Get default contactSort for sort the list of buddies
     * @param context Context to call
     * @return the sort
     */
    public static ContactSort getDefaultContactSort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prefContactSort = prefs.getString(context.getString(R.string.pref_contacts_sort_list_key),
                context.getString(R.string.pref_contacts_sort_list_value_default));
        String prefContactOrder = prefs.getString(context.getString(R.string.pref_contacts_order_list_key),
                context.getString(R.string.pref_contacts_order_list_value_default));
        return ContactSort.findContactSortByResourceValueString(
                context.getResources(),
                prefContactSort,
                prefContactOrder);
    }

    /**
     * Return true if daemons for powerfull notification or auto send message is active
     * @param context Context to call
     * @return Daemons active
     */
    public static boolean isDaemonsActive(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_special_service_key), false);
    }

    /**
     * Return true if reminders for auto-SMS is active
     * @param context Context to call
     * @return Reminders active
     */
    public static boolean isAutoSmsRemindersActive(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_auto_sms_reminders_key), false);
    }

    /**
     * Return true if buttons for inactive features are hidden
     * @param context Context to call
     * @return Hidden buttons
     */
    public static boolean isButtonsForInactiveFeaturesHidden(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_hide_inactive_features_key), false);
    }
}
