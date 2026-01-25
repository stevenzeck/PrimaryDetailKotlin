package com.example.primarydetailkotlin.settings

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class ThemeHelperTest {

    @Test
    fun applyTheme_lightMode_setsNightModeNo() {
        ThemeHelper.applyTheme(themePref = "light")
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.getDefaultNightMode())
    }

    @Test
    fun applyTheme_darkMode_setsNightModeYes() {
        ThemeHelper.applyTheme(themePref = "dark")
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.getDefaultNightMode())
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun applyTheme_defaultMode_atAndroidQ_setsFollowSystem() {
        ThemeHelper.applyTheme(themePref = "default")
        assertEquals(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, AppCompatDelegate.getDefaultNightMode()
        )
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.P])
    fun applyTheme_defaultMode_belowAndroidQ_setsAutoBattery() {
        ThemeHelper.applyTheme(themePref = "default")
        assertEquals(
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, AppCompatDelegate.getDefaultNightMode()
        )
    }
}
