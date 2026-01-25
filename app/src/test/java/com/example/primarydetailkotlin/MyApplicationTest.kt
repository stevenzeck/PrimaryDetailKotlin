package com.example.primarydetailkotlin

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.primarydetailkotlin.settings.ThemeHelper
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = MyApplication::class)
class MyApplicationTest {

    private lateinit var application: MyApplication

    @Before
    fun setup() {
        mockkObject(ThemeHelper)
        application = ApplicationProvider.getApplicationContext<MyApplication>()
    }

    @After
    fun tearDown() {
        unmockkObject(ThemeHelper)
    }

    @Test
    fun onCreate_appliesThemeFromPreferences() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs.edit().putString("themePref", "dark").commit()

        application.onCreate()

        verify { ThemeHelper.applyTheme("dark") }
    }

    @Test
    fun onCreate_appliesDefaultTheme_whenNoPreferenceSet() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs.edit().remove("themePref").commit()

        application.onCreate()

        verify { ThemeHelper.applyTheme(ThemeHelper.DEFAULT_MODE) }
    }
}
