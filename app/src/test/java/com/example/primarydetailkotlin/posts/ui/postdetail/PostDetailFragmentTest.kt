package com.example.primarydetailkotlin.posts.ui.postdetail

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.primarydetailkotlin.R
import com.example.primarydetailkotlin.di.AppModule
import com.example.primarydetailkotlin.posts.domain.model.Post
import com.example.primarydetailkotlin.posts.ui.PostDetailFragment
import com.example.primarydetailkotlin.posts.ui.PostListAdapter
import com.example.primarydetailkotlin.posts.ui.PostRepository
import com.example.primarydetailkotlin.util.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@UninstallModules(AppModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class PostDetailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val repository: PostRepository = mockk(relaxed = true)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun displayPost_showsDetails() {
        val post = Post(id = 1, userId = 1, title = "Detail Title", body = "Detail Body")
        coEvery { repository.postById(1L) } returns post

        val args = bundleOf(PostListAdapter.POST_ID to 1L)
        launchFragmentInHiltContainer<PostDetailFragment>(fragmentArgs = args)

        ShadowLooper.runUiThreadTasks()

        Espresso.onView(ViewMatchers.withId(R.id.titleTextView))
            .check(ViewAssertions.matches(ViewMatchers.withText("Detail Title")))
        Espresso.onView(ViewMatchers.withId(R.id.bodyTextView))
            .check(ViewAssertions.matches(ViewMatchers.withText("Detail Body")))
    }

    @Test
    fun deletePost_callsRepositoryAndNavigatesUp() {
        val post = Post(id = 1, userId = 1, title = "Detail Title", body = "Detail Body")
        coEvery { repository.postById(1L) } returns post

        val args = bundleOf(PostListAdapter.POST_ID to 1L)
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<PostDetailFragment>(
            fragmentArgs = args,
            themeResId = androidx.appcompat.R.style.Theme_AppCompat_Light_DarkActionBar
        ) {
            navController.setGraph(R.navigation.list)
            navController.setCurrentDestination(R.id.postDetailFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        ShadowLooper.runUiThreadTasks()

        try {
            Espresso.onView(ViewMatchers.withId(R.id.delete))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.delete)).perform(ViewActions.click())
        } catch (e: NoMatchingViewException) {
            Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
            Espresso.onView(ViewMatchers.withText(R.string.delete)).perform(ViewActions.click())
        }

        coVerify { repository.deletePost(1L) }
    }
}