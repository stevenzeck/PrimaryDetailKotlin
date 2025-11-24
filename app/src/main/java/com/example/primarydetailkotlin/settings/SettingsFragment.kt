package com.example.primarydetailkotlin.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.primarydetailkotlin.R

/**
 * Fragment responsible for displaying the application settings.
 *
 * It uses the AndroidX Preference library to load settings from an XML resource.
 * It currently handles the theme preference allowing users to switch between Light, Dark, or System Default modes.
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from the XML resource.
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Find the theme preference and set a listener to apply the theme immediately when changed.
        val themePreference = findPreference<ListPreference>("themePref")
        if (themePreference != null) {
            themePreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val themeOption = newValue as String
                    ThemeHelper.applyTheme(themeOption)
                    true
                }
        }
    }
}
