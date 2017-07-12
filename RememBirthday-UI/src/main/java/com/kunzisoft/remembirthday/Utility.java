package com.kunzisoft.remembirthday;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for generic methods
 */
public class Utility {

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

    /**
     * Open calendar application at specific time
     * @param context Context to call
     * @param date Date to show in calendar
     */
    public static void openCalendarAt(Context context, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, calendar.getTimeInMillis());
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        try {
            context.startActivity(intent);
        } catch(ActivityNotFoundException e) {
            Log.e(context.getClass().getSimpleName(), e.toString());
        }
    }
}
