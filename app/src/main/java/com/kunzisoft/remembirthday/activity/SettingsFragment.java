package com.kunzisoft.remembirthday.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 13/04/17.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
