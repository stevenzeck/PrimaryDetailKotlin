package com.example.primarydetailkotlin.posts.ui.postlist

import androidx.recyclerview.widget.RecyclerView
import com.example.primarydetailkotlin.posts.ui.RecyclerViewIdKeyProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class RecyclerViewIdKeyProviderTest {

    @Test
    fun getKey_returnsItemIdFromAdapter() {
        val recyclerView = mockk<RecyclerView>()
        val adapter = mockk<RecyclerView.Adapter<*>>()
        every { recyclerView.adapter } returns adapter
        every { adapter.getItemId(any()) } returns 100L

        val provider = RecyclerViewIdKeyProvider(recyclerView = recyclerView)
        val result = provider.getKey(position = 0)

        assertEquals(100L, result)
    }

    @Test(expected = IllegalStateException::class)
    fun getKey_throwsIfAdapterIsNull() {
        val recyclerView = mockk<RecyclerView>()
        every { recyclerView.adapter } returns null

        val provider = RecyclerViewIdKeyProvider(recyclerView = recyclerView)
        provider.getKey(position = 0)
    }

    @Test
    fun getPosition_returnsLayoutPositionFromViewHolder() {
        val recyclerView = mockk<RecyclerView>()
        val viewHolder = mockk<RecyclerView.ViewHolder>()
        every { recyclerView.findViewHolderForItemId(any()) } returns viewHolder
        every { viewHolder.layoutPosition } returns 5

        val provider = RecyclerViewIdKeyProvider(recyclerView = recyclerView)
        val result = provider.getPosition(key = 100L)

        assertEquals(5, result)
    }

    @Test
    fun getPosition_returnsNoPositionIfViewHolderNotFound() {
        val recyclerView = mockk<RecyclerView>()
        every { recyclerView.findViewHolderForItemId(any()) } returns null

        val provider = RecyclerViewIdKeyProvider(recyclerView = recyclerView)
        val result = provider.getPosition(key = 100L)

        assertEquals(RecyclerView.NO_POSITION, result)
    }
}
