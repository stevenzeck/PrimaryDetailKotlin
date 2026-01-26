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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PostsDaoTest {

    private lateinit var db: PostsDatabase
    private lateinit var dao: PostsDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context = context, klass = PostsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.postsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetPosts() = runTest {
        val post = Post(id = 1, userId = 1, title = "Title", body = "Body")
        dao.insertPosts(posts = listOf(post))

        val posts = dao.getAllPosts().first()
        assertEquals(1, posts.size)
        assertEquals(post, posts[0])
    }

    @Test
    fun getPostsCount_returnsCorrectCount() = runTest {
        val posts = listOf(
            Post(id = 1, userId = 1, title = "T1", body = "B1"),
            Post(id = 2, userId = 1, title = "T2", body = "B2")
        )
        dao.insertPosts(posts)

        val count = dao.getPostsCount()
        assertEquals(2, count)
    }

    @Test
    fun markRead_updatesReadStatus() = runTest {
        val post = Post(id = 1, userId = 1, title = "T", body = "B", read = false)
        dao.insertPosts(posts = listOf(post))

        dao.markRead(postId = 1L)

        val retrievedPost = dao.postById(postId = 1L)
        assertEquals(true, retrievedPost.read)
    }

    @Test
    fun markRead_list_updatesReadStatus() = runTest {
        val posts = listOf(
            Post(id = 1, userId = 1, title = "T1", body = "B1", read = false),
            Post(id = 2, userId = 1, title = "T2", body = "B2", read = false)
        )
        dao.insertPosts(posts = posts)

        dao.markRead(postIds = listOf(1L, 2L))

        val p1 = dao.postById(postId = 1L)
        val p2 = dao.postById(postId = 2L)
        assertEquals(true, p1.read)
        assertEquals(true, p2.read)
    }

    @Test
    fun deletePost_removesPost() = runTest {
        val post = Post(id = 1, userId = 1, title = "T", body = "B")
        dao.insertPosts(posts = listOf(post))

        dao.deletePost(postId = 1L)

        val count = dao.getPostsCount()
        assertEquals(0, count)
    }

    @Test
    fun deletePosts_removesPosts() = runTest {
        val posts = listOf(
            Post(id = 1, userId = 1, title = "T1", body = "B1"),
            Post(id = 2, userId = 1, title = "T2", body = "B2")
        )
        dao.insertPosts(posts)

        dao.deletePosts(postIds = listOf(1L, 2L))

        val count = dao.getPostsCount()
        assertEquals(0, count)
    }
}
