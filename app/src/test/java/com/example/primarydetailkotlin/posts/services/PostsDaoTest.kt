package com.example.primarydetailkotlin.posts.services

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.primarydetailkotlin.posts.domain.model.Post
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.uuid.Uuid

@RunWith(AndroidJUnit4::class)
class PostsDaoTest {

    private lateinit var db: PostsDatabase
    private lateinit var dao: PostsDao

    private fun generateMockPost(
        title: String,
        body: String,
        id: Long = Uuid.random().hashCode().toLong(),
        read: Boolean = false
    ): Post {
        return Post(id = id, userId = 1, title = title, body = body, read = read)
    }

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context = context, klass = PostsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.postsDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetPosts() = runTest {
        val post = generateMockPost(title = "Title", body = "Body")
        dao.insertPosts(posts = listOf(post))

        val posts = dao.getAllPosts().first()
        assertEquals(1, posts.size)
        assertEquals(post, posts[0])
    }

    @Test
    fun getPostsCount_returnsCorrectCount() = runTest {
        val posts = listOf(
            generateMockPost(title = "T1", body = "B1"),
            generateMockPost(title = "T2", body = "B2")
        )
        dao.insertPosts(posts)

        val count = dao.getPostsCount()
        assertEquals(2, count)
    }

    @Test
    fun markRead_updatesReadStatus() = runTest {
        val post = generateMockPost(title = "T", body = "B", read = false)
        dao.insertPosts(posts = listOf(post))

        dao.markRead(postId = post.id)

        val retrievedPost = dao.postById(postId = post.id)
        assertEquals(true, retrievedPost.read)
    }

    @Test
    fun markRead_list_updatesReadStatus() = runTest {
        val p1 = generateMockPost(title = "T1", body = "B1", read = false)
        val p2 = generateMockPost(title = "T2", body = "B2", read = false)
        dao.insertPosts(posts = listOf(p1, p2))

        dao.markRead(postIds = listOf(p1.id, p2.id))

        val retrievedP1 = dao.postById(postId = p1.id)
        val retrievedP2 = dao.postById(postId = p2.id)
        assertEquals(true, retrievedP1.read)
        assertEquals(true, retrievedP2.read)
    }

    @Test
    fun deletePost_removesPost() = runTest {
        val post = generateMockPost(title = "T", body = "B")
        dao.insertPosts(posts = listOf(post))

        dao.deletePost(postId = post.id)

        val count = dao.getPostsCount()
        assertEquals(0, count)
    }

    @Test
    fun deletePosts_removesPosts() = runTest {
        val p1 = generateMockPost(title = "T1", body = "B1")
        val p2 = generateMockPost(title = "T2", body = "B2")
        dao.insertPosts(listOf(p1, p2))

        dao.deletePosts(postIds = listOf(p1.id, p2.id))

        val count = dao.getPostsCount()
        assertEquals(0, count)
    }

    @Test
    fun getAllPosts_returnsPostsSortedByIdDescending() = runTest {
        val posts = listOf(
            generateMockPost(title = "T10", body = "B10", id = 10L),
            generateMockPost(title = "T30", body = "B30", id = 30L),
            generateMockPost(title = "T20", body = "B20", id = 20L)
        )
        dao.insertPosts(posts)

        val retrievedPosts = dao.getAllPosts().first()
        assertTrue(retrievedPosts.isSortedByDescending { it.id })
    }
}
