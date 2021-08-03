package com.kunzisoft.remembirthday.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;

import com.kunzisoft.androidclearchroma.ChromaPreferenceFragmentCompat;
import com.kunzisoft.remembirthday.BuildConfig;
import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.account.AccountResolver;
import com.kunzisoft.remembirthday.account.CalendarAccount;
import com.kunzisoft.remembirthday.preference.DaysExplanationPreference;
import com.kunzisoft.remembirthday.preference.DaysExplanationPreferenceDialogFragmentCompat;
import com.kunzisoft.remembirthday.preference.PreferencesHelper;
import com.kunzisoft.remembirthday.preference.TimePreferenceDialogFragmentCompat;
import com.kunzisoft.remembirthday.service.MainIntentService;
import com.kunzisoft.remembirthday.utility.IntentCall;

import java.util.Date;

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
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG_FRAGMENT_DIALOG = "com.kunzisoft.remembirthday.TAG_FRAGMENT_DIALOG";

    public static final int SETTING_RESULT_CODE = 1647;

    private Preference.OnPreferenceClickListener onPreferenceProFeatureClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            ((TwoStatePreference) preference).setChecked(false);
            if (getFragmentManager() != null)
                new ProFeatureDialogFragment().show(getFragmentManager(), "PRO_FEATURE_TAG");
            return false;
        }
    };

    private AccountResolver accountResolver;
    private TwoStatePreference preferenceCreateCalendar;
    private Preference.OnPreferenceClickListener onPreferenceCalendarClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            SettingsFragmentPermissionsDispatcher.onPreferenceCalendarPermissionClickWithPermissionCheck(SettingsFragment.this);
            onPreferenceCalendarPermissionClick();
            return false;
        }
    };

    private TwoStatePreference preferenceSpecial;
    private Preference.OnPreferenceClickListener onPreferenceSendSmsClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            SettingsFragmentPermissionsDispatcher.onPreferenceSendSmsPermissionClickWithPermissionCheck(SettingsFragment.this);
            onPreferenceSendSmsPermissionClick();
            return false;
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        Preference openCalendar = findPreference(getString(R.string.pref_open_calendar_key));
        openCalendar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (getContext() != null)
                    IntentCall.openCalendarAt(getContext(), new Date());
                return true;
            }
        });

        accountResolver = CalendarAccount.getAccount(getContext());
        preferenceCreateCalendar = (TwoStatePreference) findPreference(getString(R.string.pref_create_calendar_key));
        preferenceCreateCalendar.setDefaultValue(false);
        preferenceCreateCalendar.setOnPreferenceClickListener(onPreferenceCalendarClick);

        DaysExplanationPreference preferenceDefaultDays =
                (DaysExplanationPreference) findPreference(getString(R.string.pref_reminders_days_key));
        preferenceDefaultDays.setSummary(getString(R.string.pref_reminders_days_summary,
                PreferencesHelper.getDefaultDaysAsString(getContext())));

        preferenceSpecial = (TwoStatePreference) findPreference(getString(R.string.pref_special_service_key));
        preferenceSpecial.setDefaultValue(false);
        if (!BuildConfig.FULL_VERSION) {
            // Disable switch and show pro dialog if free version
            preferenceSpecial.setOnPreferenceClickListener(onPreferenceProFeatureClick);
        } else {
            preferenceSpecial.setOnPreferenceClickListener(onPreferenceSendSmsClick);
        }

        TwoStatePreference preferenceHideInactive =
                (TwoStatePreference) findPreference(getString(R.string.pref_hide_inactive_features_key));
        if (!BuildConfig.FULL_VERSION) {
            preferenceHideInactive.setOnPreferenceClickListener(onPreferenceProFeatureClick);
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_CALENDAR)
    public void onPreferenceCalendarPermissionClick() {
        if (!preferenceCreateCalendar.isChecked()) {
            preferenceCreateCalendar.setChecked(true);
            accountResolver.addAccountAndSync();
        } else {
            preferenceCreateCalendar.setChecked(false);
            accountResolver.removeAccount();
        }
    }

    @NeedsPermission(Manifest.permission.SEND_SMS)
    public void onPreferenceSendSmsPermissionClick() {
        // TODO Send SMS services
        if (!preferenceSpecial.isChecked()) {
            preferenceSpecial.setChecked(true);
        } else {
            preferenceSpecial.setChecked(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;

        if (preference.getKey().equals(getString(R.string.pref_reminders_time_key))) {
            dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
        }

        if (preference.getKey().equals(getString(R.string.pref_reminders_days_key))) {
            dialogFragment = DaysExplanationPreferenceDialogFragmentCompat.newInstance(preference.getKey());
        }

        // Show dialog
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            if (getFragmentManager() != null)
                dialogFragment.show(getFragmentManager(), TAG_FRAGMENT_DIALOG);
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

        // Update list after change sort or order
        if (key.equals(getString(R.string.pref_contacts_sort_list_key))
                || key.equals(getString(R.string.pref_contacts_order_list_key))) {
            if (getActivity() != null)
                getActivity().setResult(Activity.RESULT_OK);
        }

        // set new color
        if (key.equals(getString(R.string.pref_calendar_color_key))) {
            MainIntentService.startServiceAction(getContext(), MainIntentService.ACTION_CHANGE_COLOR);
        }
    }

    @OnShowRationale(Manifest.permission.WRITE_CALENDAR)
    public void showRationaleForCalendar(final PermissionRequest request) {
        if (getContext() != null)
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
    void showDeniedForCalendar() {
        Toast.makeText(getContext(), R.string.permission_write_calendar_denied, Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_CALENDAR)
    void showNeverAskForCalendar() {
        Toast.makeText(getContext(), R.string.permission_write_calendar_never_ask, Toast.LENGTH_LONG).show();
    }

    @OnShowRationale(Manifest.permission.SEND_SMS)
    public void showRationaleForSendSms(final PermissionRequest request) {
        if (getContext() != null)
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.permission_send_sms_rationale)
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

    @OnPermissionDenied(Manifest.permission.SEND_SMS)
    void showDeniedForSendSms() {
        Toast.makeText(getContext(), R.string.permission_send_sms_denied, Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.SEND_SMS)
    void showNeverAskForSendSms() {
        Toast.makeText(getContext(), R.string.permission_send_sms_never_ask, Toast.LENGTH_LONG).show();
    }
}
