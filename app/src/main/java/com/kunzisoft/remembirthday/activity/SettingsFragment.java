package com.kunzisoft.remembirthday.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.Toast;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.preference.TimePreference;
import com.kunzisoft.remembirthday.preference.TimePreferenceDialogFragmentCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fragment used to manage application preferences. <br />
 * WARNING : Use compatibility library known for display bugs
 * @see <a href="http://stackoverflow.com/questions/32070670/preferencefragmentcompat-requires-preferencetheme-to-be-set">StackOverflow Question</a>
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG_FRAGMENT_DIALOG = "com.kunzisoft.remembirthday.TAG_FRAGMENT_DIALOG";

    private EditTextPreference notificationsDaysEditTextPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        notificationsDaysEditTextPreference = (EditTextPreference) findPreference(getString(R.string.pref_notifications_days_key));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference)
    {
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            dialogFragment = new TimePreferenceDialogFragmentCompat();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
        }

        // Show dialog
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getChildFragmentManager(), TAG_FRAGMENT_DIALOG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key)
    {
        if(key.equals(getString(R.string.pref_notifications_days_key))) {
            // Only for 99 days maximum before the event
            Pattern p = Pattern.compile("^\\d{1,2}+(#\\d{1,2})*$");
            Matcher m = p.matcher(notificationsDaysEditTextPreference.getText());
            if(!m.matches()) {
                Log.e(getClass().getSimpleName(), notificationsDaysEditTextPreference.getText()
                        + " not matches " + p);
                Toast.makeText(getContext(), R.string.error_pref_notifications_days, Toast.LENGTH_LONG).show();
                SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
                sharedPreferenceEditor.putString(
                        getString(R.string.pref_notifications_days_key),
                        getString(R.string.pref_notifications_days_default));
                sharedPreferenceEditor.apply();
            }
        }
    }
}
