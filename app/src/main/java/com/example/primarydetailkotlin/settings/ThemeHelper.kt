package com.example.primarydetailkotlin.settings

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

/**
 * Helper object to manage application themes.
 *
 * This singleton object provides utility methods to apply the application's theme
 * (Light, Dark, or System Default) based on user preference or system settings.
 */
object ThemeHelper {

    private const val LIGHT_MODE = "light"
    private const val DARK_MODE = "dark"
    const val DEFAULT_MODE = "default"

    /**
     * Applies the specified theme to the application.
     *
     * @param themePref The theme preference string. Can be "light", "dark", or "default".
     */
    fun applyTheme(themePref: String) {
        when (themePref) {
            LIGHT_MODE -> {
                // Force Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            DARK_MODE -> {
                // Force Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            else -> {
                // Default: Follow System Settings on Android Q (10) and above,
                // or Auto Battery Saver on older versions.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }
}
