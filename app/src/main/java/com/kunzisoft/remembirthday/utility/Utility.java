package com.kunzisoft.remembirthday.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * Utility class for generic methods
 */
public class Utility {

    private static final String FIRST_TIME_KEY = "FIRST_TIME_KEY";

    /**
     * Utility class for setBackground and not depend to SDKVersion
     * @param view View to set background
     * @param drawable Background
     */
    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable drawable) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    /***
     * Checks that application runs first time and write flag at SharedPreferences
     * @return true if 1st time
     */
    public static boolean isFirstTime(Activity activity) {
        //TODO Possible bug
        SharedPreferences preferences = activity.getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean(FIRST_TIME_KEY, false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_TIME_KEY, true);
            editor.apply();
        }
        return !ranBefore;
    }

    /**
     * Assign custom phrase in TextView to describe the number of days remaining until the birthday
     * @param textView View used to display text
     * @param numberDaysRemaining Number of days left
     */
    public static void assignDaysRemainingInTextView(TextView textView, int numberDaysRemaining) {
        Resources resources = textView.getResources();
        if(numberDaysRemaining == 0) {
            textView.setText(resources.getString(R.string.dialog_select_birthday_zero_day_left));
        } else if(numberDaysRemaining == 1){
            textView.setText(resources.getString(R.string.dialog_select_birthday_one_day_left));
        } else{
            textView.setText(resources.getString(R.string.dialog_select_birthday_number_days_left, numberDaysRemaining));
        }
    }

}
