package com.example.primarydetailkotlin.posts.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.primarydetailkotlin.posts.domain.model.Post
import com.example.primarydetailkotlin.posts.services.ApiService
import com.example.primarydetailkotlin.posts.services.PostsDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PostRepositoryTest {

    private val apiService: ApiService = mockk(relaxed = true)
    private val postsDao: PostsDao = mockk(relaxed = true)
    private val repository = PostRepository(client = apiService, postsDao = postsDao)

    @Test
    fun getServerPosts_fetchesFromApi_andInsertsToDb_whenDbIsEmpty() = runTest {
        // Given
        coEvery { postsDao.getPostsCount() } returns 0
        val posts = listOf(
            Post(id = 1, userId = 1, title = "Title", body = "Body"),
            Post(id = 2, userId = 1, title = "Title 2", body = "Body 2")
        )
        coEvery { apiService.getAllPosts() } returns posts

        // When
        repository.getServerPosts()

        // Then
        coVerify { postsDao.getPostsCount() }
        coVerify { apiService.getAllPosts() }
        coVerify { postsDao.insertPosts(posts) }
    }

    @Test
    fun getServerPosts_doesNothing_whenDbIsNotEmpty() = runTest {
        // Given
        coEvery { postsDao.getPostsCount() } returns 5

        // When
        repository.getServerPosts()

        // Then
        coVerify { postsDao.getPostsCount() }
        coVerify(exactly = 0) { apiService.getAllPosts() }
        coVerify(exactly = 0) { postsDao.insertPosts(posts = any()) }
    }

    @Test(expected = IOException::class)
    fun getServerPosts_throws_whenApiFails() = runTest {
        // Given
        coEvery { postsDao.getPostsCount() } returns 0
        coEvery { apiService.getAllPosts() } throws IOException("Network Error")

        // When
        repository.getServerPosts()

        // Then exception is thrown
    }

    @Test
    fun getPostsFromDatabase_returnsFlowFromDao() = runTest {
        // Given
        val posts = listOf(Post(id = 1, userId = 1, title = "T", body = "B"))
        every { postsDao.getAllPosts() } returns flowOf(value = posts)

        // When
        val result = repository.getPostsFromDatabase().first()

        // Then
        assertEquals(posts, result)
        verify { postsDao.getAllPosts() }
    }

    @Test
    fun markRead_list_callsDao() = runTest {
        val ids = listOf(1L, 2L)
        repository.markRead(postIds = ids)
        coVerify { postsDao.markRead(postIds = ids) }
    }

    @Test
    fun markRead_single_callsDao() = runTest {
        val id = 1L
        repository.markRead(postId = id)
        coVerify { postsDao.markRead(postId = id) }
    }

    @Test
    fun deletePosts_list_callsDao() = runTest {
        val ids = listOf(1L, 2L)
        repository.deletePosts(postIds = ids)
        coVerify { postsDao.deletePosts(postIds = ids) }
    }

    @Test
    fun deletePost_single_callsDao() = runTest {
        val id = 1L
        repository.deletePost(postId = id)
        coVerify { postsDao.deletePost(postId = id) }
    }

    @Test
    fun postById_callsDao() = runTest {
        val post = Post(id = 1, userId = 1, title = "T", body = "B")
        coEvery { postsDao.postById(postId = 1L) } returns post

        val result = repository.postById(postId = 1L)

        assertEquals(post, result)
        coVerify { postsDao.postById(postId = 1L) }
    }
}
