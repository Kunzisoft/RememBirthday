package com.kunzisoft.remembirthday.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;
import android.widget.Toast;

import com.kunzisoft.androidclearchroma.ChromaPreferenceFragmentCompat;
import com.kunzisoft.remembirthday.BuildConfig;
import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.account.AccountResolver;
import com.kunzisoft.remembirthday.account.CalendarAccount;
import com.kunzisoft.remembirthday.preference.PreferencesManager;
import com.kunzisoft.remembirthday.preference.TimePreference;
import com.kunzisoft.remembirthday.preference.TimePreferenceDialogFragmentCompat;
import com.kunzisoft.remembirthday.service.MainIntentService;
import com.kunzisoft.remembirthday.utility.IntentCall;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Fragment used to manage application preferences. <br />
 * WARNING : Use compatibility library known for display bugs
 * @see <a href="http://stackoverflow.com/questions/32070670/preferencefragmentcompat-requires-preferencetheme-to-be-set">StackOverflow Question</a>
 */
@RuntimePermissions
public class SettingsFragment extends ChromaPreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String TAG_FRAGMENT_DIALOG = "com.kunzisoft.remembirthday.TAG_FRAGMENT_DIALOG";

    public static final int SETTING_RESULT_CODE = 1647;

    private EditTextPreference remindersDaysEditTextPreference;

    private AccountResolver accountResolver;

    private SwitchPreferenceCompat preferenceCreateCalendar;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        remindersDaysEditTextPreference = (EditTextPreference) findPreference(getString(R.string.pref_reminders_days_key));

        if (!BuildConfig.FULL_VERSION) {
            // Disable switch and show pro dialog if free version
            SwitchPreferenceCompat preference = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_special_service_key));
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
        preferenceCreateCalendar = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_create_calendar_key));
        preferenceCreateCalendar.setDefaultValue(false);
        preferenceCreateCalendar.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        SettingsFragmentPermissionsDispatcher.onPreferencePermissionClickWithCheck(SettingsFragment.this);
        onPreferencePermissionClick();
        return false;
    }

    @NeedsPermission(Manifest.permission.WRITE_CALENDAR)
    public void onPreferencePermissionClick() {
        if (!preferenceCreateCalendar.isChecked()) {
            preferenceCreateCalendar.setChecked(true);
            accountResolver.addAccountAndSync();
        } else {
            preferenceCreateCalendar.setChecked(false);
            accountResolver.removeAccount();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        SettingsFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
        // Verify values of notifications
        if (key.equals(getString(R.string.pref_reminders_days_key))) {
            // Only for 99 days maximum before the event
            Pattern p = Pattern.compile(PreferencesManager.PATTERN_REMINDER_PREF);
            Matcher m = p.matcher(remindersDaysEditTextPreference.getText());

            SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
            if(!m.matches()) {
                // If error in value
                Log.e(getClass().getSimpleName(), remindersDaysEditTextPreference.getText()
                        + " not matches " + p);
                Toast.makeText(getContext(), R.string.error_pref_notifications_days, Toast.LENGTH_LONG).show();
                sharedPreferenceEditor.putString(
                        getString(R.string.pref_reminders_days_key),
                        getString(R.string.pref_reminders_days_default));
            } else {
                // Save value
                sharedPreferenceEditor.putString(
                        getString(R.string.pref_reminders_days_key),
                        remindersDaysEditTextPreference.getText());
            }
            sharedPreferenceEditor.apply();
        }

        // Update list after change sort or order
        if (key.equals(getString(R.string.pref_contacts_sort_list_key))
                || key.equals(getString(R.string.pref_contacts_order_list_key))) {
            getActivity().setResult(Activity.RESULT_OK);
        }

        // set new color
        if (key.equals(getString(R.string.pref_calendar_color_key))) {
            MainIntentService.startServiceAction(getContext(), MainIntentService.ACTION_CHANGE_COLOR);
        }
    }

    @OnShowRationale(Manifest.permission.WRITE_CALENDAR)
    public void showRationaleForCalendar(final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.permission_write_calendar_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_CALENDAR)
    void showDeniedForCamera() {
        Toast.makeText(getContext(), R.string.permission_write_calendar_denied, Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_CALENDAR)
    void showNeverAskForCamera() {
        Toast.makeText(getContext(), R.string.permission_write_calendar_never_ask, Toast.LENGTH_LONG).show();
    }
}
