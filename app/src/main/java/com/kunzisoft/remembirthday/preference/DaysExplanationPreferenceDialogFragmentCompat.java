package com.kunzisoft.remembirthday.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kunzisoft.remembirthday.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PreferenceDialog for showing Days notifications selector
 */
public class DaysExplanationPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private TextView textExplanationView;
    private EditText daysInputTextView;

    public static DaysExplanationPreferenceDialogFragmentCompat newInstance(
            String key) {
        final DaysExplanationPreferenceDialogFragmentCompat
                fragment = new DaysExplanationPreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        textExplanationView = view.findViewById(R.id.explanation_text);
        daysInputTextView = view.findViewById(R.id.input_text);
        setExplanationText(getString(R.string.pref_reminders_days_explanation));
        setDaysInputText(PreferencesHelper.getDefaultDaysAsString(getContext()));
        daysInputTextView.setSelection(daysInputTextView.getText().length());
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

        if (positiveResult) {
            verify(getDaysInputText());
            getPreference().setSummary(getString(R.string.pref_reminders_days_summary,
                    PreferencesHelper.getDefaultDaysAsString(getContext())));
        }
    }

    private void verify(String daysInputText) {
        if (getContext() != null) {
            // Only for 99 days maximum before the event
            Pattern p = Pattern.compile(PreferencesHelper.PATTERN_REMINDER_PREF);
            Matcher m = p.matcher(daysInputText);

            SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager
                    .getDefaultSharedPreferences(getContext()).edit();
            if (!m.matches()) {
                // If error in value
                Log.e(getClass().getSimpleName(), daysInputText
                        + " not matches " + p);
                Toast.makeText(getContext(), R.string.error_pref_notifications_days, Toast.LENGTH_LONG).show();
                sharedPreferenceEditor.putString(
                        getString(R.string.pref_reminders_days_key),
                        getString(R.string.pref_reminders_days_default));
            } else {
                // Save value
                sharedPreferenceEditor.putString(
                        getString(R.string.pref_reminders_days_key),
                        getDaysInputText());
            }
            sharedPreferenceEditor.apply();
        }
    }

    public String getExplanationText() {
        if (textExplanationView != null)
            return textExplanationView.getText().toString();
        else
            return "";
    }

    public void setExplanationText(String explanationText) {
        if (textExplanationView != null)
            if (explanationText != null && !explanationText.isEmpty()) {
                textExplanationView.setText(explanationText);
                textExplanationView.setVisibility(View.VISIBLE);
            } else {
                textExplanationView.setText(explanationText);
                textExplanationView.setVisibility(View.VISIBLE);
            }
    }

    public String getDaysInputText() {
        return daysInputTextView.getText().toString();
    }

    public void setDaysInputText(String input) {
        daysInputTextView.setText(input);
    }
}
