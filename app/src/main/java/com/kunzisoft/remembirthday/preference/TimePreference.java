package com.kunzisoft.remembirthday.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;

import java.util.Locale;

public class TimePreference extends DialogPreference {

    private static final String TIME_TAG_SUMMARY = "[time]";
    private static final String TIME_SEPARATOR = ":";

    private int hour = 0;
    private int minute = 0;

    private CharSequence summaryPreference;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public TimePreference(Context context) {
        super(context);
        initialize(context, null);
    }

    private void initialize(Context context, AttributeSet attrs) {
        summaryPreference = getSummary();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);

        String time;
        if (restorePersistedValue) {
            if (defaultValue==null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        hour = parseHour(time);
        minute = parseMinute(time);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        updatePreview();
    }

    @Override
    public void onAttached() {
        super.onAttached();
        updatePreview();
    }

    synchronized private void updatePreview() {
        setSummary(summaryPreference);
    }

    @Override
    protected boolean persistString(String value) {
        updatePreview();
        return super.persistString(value);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /**
     * {@inheritDoc}
     * If [time] is present in summary, it's replaced by string of time
     */
    @Override
    public void setSummary(CharSequence summary) {
        String summaryWithTime = null;
        if(summary != null) {
            summaryWithTime = summary.toString().replace(TIME_TAG_SUMMARY, timeToString(hour, minute));
        }
        super.setSummary(summaryWithTime);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public static int parseHour(String time) {
        return(Integer.parseInt(time.split(TIME_SEPARATOR)[0]));
    }

    public static int parseMinute(String time) {
        return(Integer.parseInt(time.split(TIME_SEPARATOR)[1]));
    }

    public static String timeToString(int hours, int minutes) {
        return String.format(Locale.getDefault(), "%02d"+TIME_SEPARATOR+"%02d", hours, minutes);
    }
}
