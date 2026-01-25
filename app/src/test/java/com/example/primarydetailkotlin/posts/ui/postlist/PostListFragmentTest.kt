package com.example.primarydetailkotlin.posts.ui.postlist

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.primarydetailkotlin.R
import com.example.primarydetailkotlin.posts.domain.model.Post
import com.example.primarydetailkotlin.posts.ui.PostListFragment
import com.example.primarydetailkotlin.posts.ui.PostRepository
import com.example.primarydetailkotlin.util.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowLooper

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PostListFragmentTest {

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
    fun onViewCreated_callsServerPosts() {
        every { repository.getPostsFromDatabase() } returns flowOf(emptyList())
        
        launchFragmentInHiltContainer<PostListFragment>()
        ShadowLooper.runUiThreadTasks()

        coVerify { repository.getServerPosts() }
    }

    @Test
    fun displayPosts_showsInRecyclerView() {
        val post = Post(id = 1, userId = 1, title = "Test Post", body = "Body")
        every { repository.getPostsFromDatabase() } returns flowOf(listOf(post))

        launchFragmentInHiltContainer<PostListFragment>()

        // Wait for data
        ShadowLooper.runUiThreadTasks()

        onView(withId(R.id.post_list)).check(matches(hasDescendant(withText("Test Post"))))
    }

    @Test
    fun clickPost_navigatesToDetail() {
        val post = Post(id = 1, userId = 1, title = "Test Post", body = "Body")
        every { repository.getPostsFromDatabase() } returns flowOf(listOf(post))

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<PostListFragment> {
            navController.setGraph(R.navigation.list)
            navController.setCurrentDestination(R.id.postListFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        ShadowLooper.runUiThreadTasks()

        onView(withId(R.id.post_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0, click()
            )
        )

        assertEquals(R.id.postDetailFragment, navController.currentDestination?.id)

        val args = navController.backStack.last().arguments
        assertEquals(1L, args?.getLong("postId"))
    }

    @Test
    fun clickSettings_navigatesToSettings() {
        every { repository.getPostsFromDatabase() } returns flowOf(emptyList())
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<PostListFragment> {
            navController.setGraph(R.navigation.list)
            navController.setCurrentDestination(R.id.postListFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.title_settings)).perform(click())

        assertEquals(R.id.settingsFragment, navController.currentDestination?.id)
    }

    @Test
    fun selectionMode_deleteAction() {
        performSelectionAction(R.id.delete)
        coVerify { repository.deletePosts(listOf(1L)) }
    }

    @Test
    fun selectionMode_markReadAction() {
        performSelectionAction(R.id.markRead)
        coVerify { repository.markRead(listOf(1L)) }
    }

    private fun performSelectionAction(actionId: Int) {
        val post = Post(id = 1, userId = 1, title = "Test Post", body = "Body")
        every { repository.getPostsFromDatabase() } returns flowOf(listOf(post))
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        var fragment: PostListFragment? = null

        launchFragmentInHiltContainer<PostListFragment> {
            fragment = this
            navController.setGraph(R.navigation.list)
            navController.setCurrentDestination(R.id.postListFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        ShadowLooper.runUiThreadTasks()

        // Use reflection to select item
        val trackerField = PostListFragment::class.java.getDeclaredField("mSelectionTracker")
        trackerField.isAccessible = true
        @Suppress("UNCHECKED_CAST") val tracker =
            trackerField.get(fragment) as SelectionTracker<Long>
        tracker.select(1L)

        ShadowLooper.runUiThreadTasks()

        onView(withId(actionId)).perform(click())
    }
}
