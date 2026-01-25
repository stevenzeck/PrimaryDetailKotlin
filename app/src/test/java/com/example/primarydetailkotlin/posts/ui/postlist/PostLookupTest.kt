package com.example.primarydetailkotlin.posts.ui.postlist

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.primarydetailkotlin.posts.ui.PostListAdapter
import com.example.primarydetailkotlin.posts.ui.PostLookup
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class PostLookupTest {

    @Test
    fun getItemDetails_returnsDetailsFromViewHolder() {
        val recyclerView = mockk<RecyclerView>()
        val motionEvent = mockk<MotionEvent>()
        val view = mockk<View>()
        val viewHolder = mockk<PostListAdapter.ViewHolder>()

        // Mock coordinates
        every { motionEvent.x } returns 10f
        every { motionEvent.y } returns 20f

        // Mock finding child
        every { recyclerView.findChildViewUnder(10f, 20f) } returns view
        every { recyclerView.getChildViewHolder(view) } returns viewHolder

        // Mock item details
        val details = mockk<ItemDetailsLookup.ItemDetails<Long>>()
        every { viewHolder.getItemDetails() } returns details

        val lookup = PostLookup(recyclerView)
        val result = lookup.getItemDetails(motionEvent)

        Assert.assertEquals(details, result)
    }

    @Test
    fun getItemDetails_noView_returnsNull() {
        val recyclerView = mockk<RecyclerView>()
        val motionEvent = mockk<MotionEvent>()

        every { motionEvent.x } returns 10f
        every { motionEvent.y } returns 20f
        every { recyclerView.findChildViewUnder(10f, 20f) } returns null

        val lookup = PostLookup(recyclerView)
        val result = lookup.getItemDetails(motionEvent)

        Assert.assertNull(result)
    }
}