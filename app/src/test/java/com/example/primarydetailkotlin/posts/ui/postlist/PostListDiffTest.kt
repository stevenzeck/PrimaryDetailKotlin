package com.example.primarydetailkotlin.posts.ui.postlist

import com.example.primarydetailkotlin.posts.domain.model.Post
import com.example.primarydetailkotlin.posts.ui.PostListDiff
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PostListDiffTest {

    private val callback = PostListDiff()

    @Test
    fun areItemsTheSame_true_whenSameId() {
        val p1 = Post(id = 1, userId = 1, title = "T1", body = "B1")
        val p2 = Post(id = 1, userId = 1, title = "T2", body = "B2")

        assertTrue(callback.areItemsTheSame(p1, p2))
    }

    @Test
    fun areItemsTheSame_false_whenDifferentId() {
        val p1 = Post(id = 1, userId = 1, title = "T1", body = "B1")
        val p2 = Post(id = 2, userId = 1, title = "T1", body = "B1")

        assertFalse(callback.areItemsTheSame(p1, p2))
    }

    @Test
    fun areContentsTheSame_true_whenSameTitleAndRead() {
        val p1 = Post(id = 1, userId = 1, title = "T", body = "B", read = false)
        val p2 = Post(id = 1, userId = 2, title = "T", body = "B2", read = false)

        assertTrue(callback.areContentsTheSame(p1, p2))
    }

    @Test
    fun areContentsTheSame_false_whenDifferentTitle() {
        val p1 = Post(id = 1, userId = 1, title = "T1", body = "B", read = false)
        val p2 = Post(id = 1, userId = 1, title = "T2", body = "B", read = false)

        assertFalse(callback.areContentsTheSame(p1, p2))
    }

    @Test
    fun areContentsTheSame_false_whenDifferentReadStatus() {
        val p1 = Post(id = 1, userId = 1, title = "T", body = "B", read = false)
        val p2 = Post(id = 1, userId = 1, title = "T", body = "B", read = true)

        assertFalse(callback.areContentsTheSame(p1, p2))
    }
}
