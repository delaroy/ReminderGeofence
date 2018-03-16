package com.delaroystudios.geofence;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by delaroy on 3/13/18.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class GeofencePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference repInterval = findPreference(getString(R.string.settings_rep_interval_key));
            bindPreferenceSummaryToValue(repInterval);

            Preference repType = findPreference(getString(R.string.settings_rep_type_key));
            bindPreferenceSummaryToValue(repType);

            Preference repeat = findPreference(getString(R.string.settings_repeat_key));
            bindPreferenceSummaryToValue(repeat);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else if (preference instanceof SwitchPreference
                    || preference instanceof CheckBoxPreference) {
                boolean repeatState = (Boolean) value;
               preference.setDefaultValue(repeatState);
            }

            else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            if (preference instanceof ListPreference || preference instanceof EditTextPreference){
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                String preferenceString = preferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }else if (preference instanceof SwitchPreference
                    || preference instanceof CheckBoxPreference){
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                boolean prefBoolean = preferences.getBoolean(preference.getKey(), false);
                onPreferenceChange(preference, prefBoolean);

            }

        }
    }
}
