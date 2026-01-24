package com.example.primarydetailkotlin.posts.ui

import com.example.primarydetailkotlin.posts.domain.model.Post
import com.example.primarydetailkotlin.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PostViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: PostRepository = mockk(relaxed = true)

    @Test
    fun postsFlow_emitsDataFromRepository() = runTest {
        // Given
        val postsList = listOf(Post(id = 1, userId = 1, title = "T", body = "B"))
        every { repository.getPostsFromDatabase() } returns flowOf(value = postsList)
        val viewModel = PostViewModel(repository)

        // When
        val result = viewModel.posts.first()

        // Then
        assertEquals(postsList, result)
    }

    @Test
    fun serverPosts_callsRepositoryGetServerPosts() = runTest {
        // Given
        every { repository.getPostsFromDatabase() } returns flowOf(value = emptyList())
        val viewModel = PostViewModel(repository)

        // When
        viewModel.serverPosts()

        // Then
        coVerify { repository.getServerPosts() }
    }

    @Test
    fun markRead_list_callsRepository() = runTest {
        every { repository.getPostsFromDatabase() } returns flowOf(value = emptyList())
        val viewModel = PostViewModel(repository)
        val ids = listOf(1L, 2L)

        viewModel.markRead(postIds = ids)

        coVerify { repository.markRead(postIds = ids) }
    }

    @Test
    fun markRead_single_callsRepository() = runTest {
        every { repository.getPostsFromDatabase() } returns flowOf(value = emptyList())
        val viewModel = PostViewModel(repository)
        val id = 1L

        viewModel.markRead(postId = id)

        coVerify { repository.markRead(postId = id) }
    }

    @Test
    fun deletePosts_list_callsRepository() = runTest {
        every { repository.getPostsFromDatabase() } returns flowOf(value = emptyList())
        val viewModel = PostViewModel(repository)
        val ids = listOf(1L, 2L)

        viewModel.deletePosts(postIds = ids)

        coVerify { repository.deletePosts(postIds = ids) }
    }

    @Test
    fun deletePost_single_callsRepository() = runTest {
        every { repository.getPostsFromDatabase() } returns flowOf(value = emptyList())
        val viewModel = PostViewModel(repository)
        val id = 1L

        viewModel.deletePost(postId = id)

        coVerify { repository.deletePost(postId = id) }
    }
}
