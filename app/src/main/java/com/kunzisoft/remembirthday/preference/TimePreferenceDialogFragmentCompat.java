package com.kunzisoft.remembirthday.preference;

import android.content.Context;
import android.os.Build;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.TimePicker;

/**
 * PreferenceDialog for showing Time selector with hour and seconds
 */
public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private TimePicker timePicker = null;

    @Override
    public View onCreateDialogView(Context context) {
        timePicker = new TimePicker(context);
        return (timePicker);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        timePicker.setIs24HourView(true);
        TimePreference pref = (TimePreference) getPreference();
        if (Build.VERSION.SDK_INT < 23) {
            timePicker.setCurrentHour(pref.getHour());
            timePicker.setCurrentMinute(pref.getMinute());
        } else {
            timePicker.setHour(pref.getHour());
            timePicker.setMinute(pref.getMinute());
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            TimePreference pref = (TimePreference) getPreference();

            if (Build.VERSION.SDK_INT < 23) {
                pref.setHour(timePicker.getCurrentHour());
                pref.setMinute(timePicker.getCurrentMinute());
            } else {
                pref.setHour(timePicker.getHour());
                pref.setMinute(timePicker.getMinute());
            }

            String value = TimePreference.timeToString(pref.getHour(), pref.getMinute());
            if (pref.callChangeListener(value)) pref.persistString(value);
        }
    }
}
