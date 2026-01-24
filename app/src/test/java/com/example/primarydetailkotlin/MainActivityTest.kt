package com.example.primarydetailkotlin

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.primarydetailkotlin.di.DatabaseModule
import com.example.primarydetailkotlin.di.NetworkModule
import com.example.primarydetailkotlin.posts.domain.model.Post
import com.example.primarydetailkotlin.posts.services.ApiService
import com.example.primarydetailkotlin.posts.services.PostsDao
import com.example.primarydetailkotlin.posts.services.PostsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(NetworkModule::class, DatabaseModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var postsDao: PostsDao

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun activityLaunches_andDisplayPosts() {
        // Given
        val post = Post(id = 1, userId = 1, title = "Test Title", body = "Test Body")
        every { postsDao.getAllPosts() } returns flowOf(listOf(post))
        every { postsDao.getPostsCount() } returns 1

        // When
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Check if activity is running
                assertNotNull(activity)

                // Verify Toolbar is present
                val toolbar = activity.findViewById<View>(R.id.toolbar)
                assertNotNull(toolbar)
            }
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object TestModule {

        @Singleton
        @Provides
        fun provideApiService(): ApiService {
            return mockk(relaxed = true)
        }

        @Singleton
        @Provides
        fun providePostsDatabase(): PostsDatabase {
            return mockk(relaxed = true)
        }

        @Singleton
        @Provides
        fun providePostsDao(): PostsDao {
            return mockk(relaxed = true)
        }
    }
}
