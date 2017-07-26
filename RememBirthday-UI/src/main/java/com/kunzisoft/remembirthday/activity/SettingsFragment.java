package com.kunzisoft.remembirthday.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;
import android.widget.Toast;

import com.kunzisoft.remembirthday.BuildConfig;
import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.account.AccountResolver;
import com.kunzisoft.remembirthday.account.CalendarAccount;
import com.kunzisoft.remembirthday.preference.PreferencesManager;
import com.kunzisoft.remembirthday.preference.TimePreference;
import com.kunzisoft.remembirthday.preference.TimePreferenceDialogFragmentCompat;
import com.kunzisoft.remembirthday.utility.IntentCall;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fragment used to manage application preferences. <br />
 * WARNING : Use compatibility library known for display bugs
 * @see <a href="http://stackoverflow.com/questions/32070670/preferencefragmentcompat-requires-preferencetheme-to-be-set">StackOverflow Question</a>
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG_FRAGMENT_DIALOG = "com.kunzisoft.remembirthday.TAG_FRAGMENT_DIALOG";

    public static final int SETTING_RESULT_CODE = 1647;

    private EditTextPreference notificationsDaysEditTextPreference;

    private SwitchPreferenceCompat customCalendar;
    private AccountResolver accountResolver;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        notificationsDaysEditTextPreference = (EditTextPreference) findPreference(getString(R.string.pref_notifications_days_key));

        if (!BuildConfig.FULL_VERSION) {
            // Disable switch and show pro dialog if free version
            SwitchPreferenceCompat preference = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_notifications_service_key));
            preference.setDefaultValue(false);
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((SwitchPreferenceCompat) preference).setChecked(false);
                    new ProFeatureDialogFragment().show(getFragmentManager(), "PRO_FEATURE_TAG");
                    return false;
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Preference openCalendar = findPreference(getString(R.string.pref_open_calendar_key));
        openCalendar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @SuppressLint("NewApi")
            @Override
            public boolean onPreferenceClick(Preference preference) {
                IntentCall.openCalendarAt(getContext(), new Date());
                return true;
            }
        });

        accountResolver = CalendarAccount.getAccount(getContext());
        customCalendar = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_create_calendar_key));
        customCalendar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    Boolean boolVal = (Boolean) newValue;
                    if (boolVal) {
                        accountResolver.addAccountAndSync();
                    } else {
                        accountResolver.removeAccount();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
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

        // If account is activated check the preference
        /* TODO account
        if (accountHelper.isAccountActivated()) {
            customCalendar.setChecked(true);
        } else {
            customCalendar.setChecked(false);
        }
        */
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
        // Verify values of notifications
        if(key.equals(getString(R.string.pref_notifications_days_key))) {
            // Only for 99 days maximum before the event
            Pattern p = Pattern.compile(PreferencesManager.PATTERN_REMINDER_PREF);
            Matcher m = p.matcher(notificationsDaysEditTextPreference.getText());

            SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
            if(!m.matches()) {
                // If error in value
                Log.e(getClass().getSimpleName(), notificationsDaysEditTextPreference.getText()
                        + " not matches " + p);
                Toast.makeText(getContext(), R.string.error_pref_notifications_days, Toast.LENGTH_LONG).show();
                sharedPreferenceEditor.putString(
                        getString(R.string.pref_notifications_days_key),
                        getString(R.string.pref_notifications_days_default));
            } else {
                // Save value
                sharedPreferenceEditor.putString(
                        getString(R.string.pref_notifications_days_key),
                        notificationsDaysEditTextPreference.getText());
            }
            sharedPreferenceEditor.apply();
        }

        // Update list after change sort or order
        if(key.equals(getString(R.string.pref_contacts_sort_list_key))
                || key.equals(getString(R.string.pref_contacts_order_list_key))) {
            getActivity().setResult(Activity.RESULT_OK);
        }
    }
}
