package com.kunzisoft.remembirthday.utility;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;

/**
 * Utility class for generic methods
 */
public class Utility {

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
