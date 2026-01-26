package com.example.primarydetailkotlin.di

import com.example.primarydetailkotlin.posts.services.ApiService
import com.example.primarydetailkotlin.posts.services.PostsDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
class DependencyInjectionTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var postsDao: PostsDao

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun verifyDependenciesAreInjected() {
        assertNotNull(apiService)
        assertNotNull(postsDao)
    }
}
