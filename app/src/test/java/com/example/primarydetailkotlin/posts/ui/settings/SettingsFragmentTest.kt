package com.example.primarydetailkotlin.posts.ui.settings

import androidx.preference.ListPreference
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.primarydetailkotlin.settings.SettingsFragment
import com.example.primarydetailkotlin.settings.ThemeHelper
import com.example.primarydetailkotlin.util.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class SettingsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        mockkObject(ThemeHelper)
    }

    @After
    fun tearDown() {
        unmockkObject(ThemeHelper)
    }

    @Test
    fun changeThemePreference_callsThemeHelper() {
        launchFragmentInHiltContainer<SettingsFragment> {
            val preference = findPreference<ListPreference>("themePref")
            preference?.value = "dark"
            preference?.callChangeListener("dark")
        }

        verify { ThemeHelper.applyTheme(themePref = "dark") }
    }
}