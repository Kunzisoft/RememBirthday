package com.kunzisoft.remembirthday.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.factory.ContactSort;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to retrieve items from preferences
 * @author joker on 05/07/17.
 */
public class PreferencesManager {

    public static final String PATTERN_REMINDER_PREF = "^\\d{1,2}+(#\\d{1,2})*$";

    /**
     * Get default days in preferences
     * @param context Context to call
     * @return Array of days
     */
    public static int[] getDefaultDays(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prefNotificationsDay = prefs.getString(context.getString(R.string.pref_notifications_days_key),
                context.getString(R.string.pref_notifications_days_default));
        Pattern p = Pattern.compile(PATTERN_REMINDER_PREF);
        int[] days;
        Matcher m = p.matcher(prefNotificationsDay);
        if(m.matches()) {
            String[] splitString = prefNotificationsDay.split("#");
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
     * Get default time in preferences
     * @param context Context to call
     * @return Array of 2 elements, [0] for hours, [1] for minutes
     */
    public static int[] getDefaultTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prefNotificationsTime = prefs.getString(context.getString(R.string.pref_notifications_time_key),
                context.getString(R.string.pref_notifications_time_default));
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
        return prefs.getBoolean(context.getString(R.string.pref_notifications_service_key), false);
    }
}
