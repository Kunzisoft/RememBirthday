package com.kunzisoft.remembirthday;

import android.content.res.Resources;
import android.widget.TextView;

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
}
