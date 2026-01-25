package com.example.primarydetailkotlin.posts.ui.postlist

import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
@Config(sdk = [34])
class PostListAdapterTest {

    @Before
    fun setup() {
        mockkStatic(Navigation::class)
        every { Navigation.findNavController(any()) } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkStatic(Navigation::class)
    }

    @Test
    fun bind_setsTitleAndBoldness_unread() {
        val adapter = PostListAdapter {}
        val parent = LinearLayout(ApplicationProvider.getApplicationContext())
        val viewHolder = adapter.onCreateViewHolder(parent, 0)

        val post = Post(id = 1, userId = 1, title = "Title", body = "Body", read = false)

        val latch = CountDownLatch(1)
        adapter.submitList(listOf(post)) { latch.countDown() }
        latch.await(2, TimeUnit.SECONDS)
        ShadowLooper.idleMainLooper()

        adapter.onBindViewHolder(viewHolder, 0)

        val titleView = viewHolder.itemView.findViewById<TextView>(R.id.postTitle)
        assertEquals("Title", titleView.text.toString())
    }

    @Test
    fun click_invokesCallback() {
        val callback = mockk<(Long) -> Unit>(relaxed = true)
        val adapter = PostListAdapter(callback)
        val parent = LinearLayout(ApplicationProvider.getApplicationContext())
        val viewHolder = adapter.onCreateViewHolder(parent, 0)

        val post = Post(id = 1, userId = 1, title = "Title", body = "Body", read = false)
        val latch = CountDownLatch(1)
        adapter.submitList(listOf(post)) { latch.countDown() }
        latch.await(2, TimeUnit.SECONDS)
        ShadowLooper.idleMainLooper()

        adapter.onBindViewHolder(viewHolder, 0)

        assertTrue(viewHolder.itemView.hasOnClickListeners())
        viewHolder.itemView.callOnClick()

        verify { callback(1L) }
    }

    @Test
    fun getItemId_returnsPostId() {
        val adapter = PostListAdapter {}
        val post = Post(id = 123L, userId = 1, title = "Title", body = "Body")
        val latch = CountDownLatch(1)
        adapter.submitList(listOf(post)) { latch.countDown() }
        latch.await(2, TimeUnit.SECONDS)
        ShadowLooper.idleMainLooper()

        assertEquals(123L, adapter.getItemId(0))
    }
}
