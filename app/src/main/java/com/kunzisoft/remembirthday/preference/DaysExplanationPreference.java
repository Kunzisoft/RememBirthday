package com.kunzisoft.remembirthday.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.kunzisoft.remembirthday.R;

public class DaysExplanationPreference extends DialogPreference {

    public DaysExplanationPreference(Context context) {
        this(context, null);
    }

    public DaysExplanationPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dialogPreferenceStyle);
    }

    public DaysExplanationPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public DaysExplanationPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.pref_dialog_input_days_explanation;
    }
}
