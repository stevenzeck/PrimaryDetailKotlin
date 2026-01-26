package com.example.primarydetailkotlin.posts.ui.postlist

import android.graphics.Typeface
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.primarydetailkotlin.MyApplication
import com.example.primarydetailkotlin.R
import com.example.primarydetailkotlin.posts.domain.model.Post
import com.example.primarydetailkotlin.posts.ui.PostListAdapter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@Config(application = MyApplication::class)
class PostListAdapterTest {

    private lateinit var recyclerView: RecyclerView

    @Before
    fun setup() {
        mockkStatic(Navigation::class)
        recyclerView = RecyclerView(ApplicationProvider.getApplicationContext())
        recyclerView.layoutManager =
            LinearLayoutManager(ApplicationProvider.getApplicationContext())
        recyclerView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY)
        )
        recyclerView.layout(0, 0, 1000, 1000)
    }

    @After
    fun tearDown() {
        unmockkStatic(Navigation::class)
    }

    @Test
    fun bind_setsTitleAndBoldness_unread() {
        val adapter = PostListAdapter {}
        recyclerView.adapter = adapter

        val post = Post(id = 1, userId = 1, title = "Title", body = "Body", read = false)
        submitAndIdle(adapter, listOf(post))

        val viewHolder =
            recyclerView.findViewHolderForAdapterPosition(0) as PostListAdapter.ViewHolder
        adapter.onBindViewHolder(viewHolder, 0)

        val titleView = viewHolder.itemView.findViewById<TextView>(R.id.postTitle)
        assertEquals("Title", titleView.text.toString())
        val style = titleView.typeface?.style ?: Typeface.NORMAL
        assertEquals(Typeface.BOLD, style)
    }

    @Test
    fun bind_setsTitleAndNormal_read() {
        val adapter = PostListAdapter {}
        recyclerView.adapter = adapter

        val post = Post(id = 1, userId = 1, title = "Title", body = "Body", read = true)
        submitAndIdle(adapter, listOf(post))

        val viewHolder =
            recyclerView.findViewHolderForAdapterPosition(0) as PostListAdapter.ViewHolder
        adapter.onBindViewHolder(viewHolder, 0)

        val titleView = viewHolder.itemView.findViewById<TextView>(R.id.postTitle)
        val style = titleView.typeface?.style ?: Typeface.NORMAL
        assertEquals(Typeface.NORMAL, style)
    }

    @Test
    fun bind_setsActivated_whenSelectedInTracker() {
        val adapter = PostListAdapter {}
        val tracker = mockk<SelectionTracker<Long>>()
        adapter.mTracker = tracker
        every { tracker.isSelected(1L) } returns true

        recyclerView.adapter = adapter
        val post = Post(id = 1, userId = 1, title = "Title", body = "Body")
        submitAndIdle(adapter, listOf(post))

        val viewHolder =
            recyclerView.findViewHolderForAdapterPosition(0) as PostListAdapter.ViewHolder
        adapter.onBindViewHolder(viewHolder, 0)

        assertTrue(viewHolder.itemView.isActivated)
    }

    @Test
    fun click_invokesCallback_andNavigates_phoneMode() {
        val callback = mockk<(Long) -> Unit>(relaxed = true)
        val adapter = PostListAdapter(callback)
        recyclerView.adapter = adapter

        val post = Post(id = 1, userId = 1, title = "Title", body = "Body", read = false)
        submitAndIdle(adapter, listOf(post))

        val viewHolder =
            recyclerView.findViewHolderForAdapterPosition(0) as PostListAdapter.ViewHolder
        adapter.onBindViewHolder(viewHolder, 0)

        val mockNavController = mockk<NavController>(relaxed = true)
        every { Navigation.findNavController(any<View>()) } returns mockNavController

        viewHolder.itemView.performClick()

        verify { callback(1L) }
        verify {
            mockNavController.navigate(
                R.id.action_postListFragment_to_postDetailFragment,
                any()
            )
        }
    }

    @Test
    fun click_navigates_tabletMode() {
        val adapter = PostListAdapter {}

        val root = FrameLayout(ApplicationProvider.getApplicationContext())
        val detailContainer = View(ApplicationProvider.getApplicationContext()).apply {
            id = R.id.post_detail_container
        }
        root.addView(detailContainer)
        root.addView(recyclerView)

        recyclerView.adapter = adapter

        val post = Post(id = 1, userId = 1, title = "Title", body = "Body")
        submitAndIdle(adapter, listOf(post))

        root.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY)
        )
        root.layout(0, 0, 1000, 1000)

        val viewHolder =
            recyclerView.findViewHolderForAdapterPosition(0) as PostListAdapter.ViewHolder
        adapter.onBindViewHolder(viewHolder, 0)

        val mockNavController = mockk<NavController>(relaxed = true)
        every { Navigation.findNavController(detailContainer) } returns mockNavController
        every { Navigation.findNavController(viewHolder.itemView) } returns mockNavController

        viewHolder.itemView.performClick()

        verify { mockNavController.navigate(R.id.postDetailFragmentPane, any()) }
    }

    @Test
    fun getItemId_returnsPostId() {
        val adapter = PostListAdapter {}
        val post = Post(id = 123L, userId = 1, title = "Title", body = "Body")
        submitAndIdle(adapter, listOf(post))

        assertEquals(123L, adapter.getItemId(0))
    }

    @Test
    fun getItemDetails_returnsCorrectKeyAndPosition() {
        val adapter = PostListAdapter {}
        recyclerView.adapter = adapter

        val post = Post(id = 123L, userId = 1, title = "T", body = "B")
        submitAndIdle(adapter, listOf(post))

        val viewHolder =
            recyclerView.findViewHolderForAdapterPosition(0) as PostListAdapter.ViewHolder
        adapter.onBindViewHolder(viewHolder, 0)

        val details = viewHolder.getItemDetails()
        assertEquals(123L, details.selectionKey)
        assertEquals(0, details.position)
    }

    private fun submitAndIdle(adapter: PostListAdapter, list: List<Post>) {
        val latch = CountDownLatch(1)
        adapter.submitList(list) { latch.countDown() }
        latch.await(2, TimeUnit.SECONDS)
        ShadowLooper.idleMainLooper()
        recyclerView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY)
        )
        recyclerView.layout(0, 0, 1000, 1000)
    }
}
