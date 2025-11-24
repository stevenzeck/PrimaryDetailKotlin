package com.example.primarydetailkotlin

import android.app.Application
import androidx.preference.PreferenceManager
import com.example.primarydetailkotlin.settings.ThemeHelper
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for the project.
 *
 * This class serves as the entry point for the application and is responsible for
 * initializing global configurations such as dependency injection (Hilt),
 * theme preferences, and dynamic colors.
 */
@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Shared Preferences to retrieve the user's theme preference.
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Retrieve the saved theme preference (defaulting to System default) and apply it.
        val themePref = sharedPreferences.getString("themePref", ThemeHelper.DEFAULT_MODE)
        themePref?.let { ThemeHelper.applyTheme(it) }

        // Apply Material 3 Dynamic Colors to all activities in the application if supported by the device.
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
