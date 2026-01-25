package com.example.primarydetailkotlin.di

import com.example.primarydetailkotlin.posts.services.ApiService
import com.example.primarydetailkotlin.posts.services.PostsDao
import com.example.primarydetailkotlin.posts.ui.PostRepository
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

    // PostRepository is ViewModelScoped, so we can't inject it directly in a Singleton test
    // without more setup, but we can verify the Singleton components.

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
