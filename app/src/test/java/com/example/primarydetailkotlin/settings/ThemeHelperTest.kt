package com.example.primarydetailkotlin.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

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
    fun applyTheme_defaultMode_setsFollowSystem_orAutoBattery() {
        ThemeHelper.applyTheme(themePref = "default")

        // Assuming SDK >= Q (29)
        assertEquals(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, AppCompatDelegate.getDefaultNightMode()
        )
    }
}
