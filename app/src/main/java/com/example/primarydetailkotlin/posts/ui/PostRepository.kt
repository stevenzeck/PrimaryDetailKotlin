package com.example.primarydetailkotlin.posts.ui

import android.util.Log
import com.example.primarydetailkotlin.posts.domain.model.Post
import com.example.primarydetailkotlin.posts.services.ApiService
import com.example.primarydetailkotlin.posts.services.PostsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository class responsible for data handling.
 *
 * This class abstracts the data sources (Network and Database) from the ViewModel.
 * It determines whether to fetch data from the network or the local database and synchronizes them.
 *
 * @property client The API service for network requests.
 * @property postsDao The Data Access Object for local database operations.
 */
class PostRepository @Inject constructor(
    private val client: ApiService,
    private val postsDao: PostsDao
) {

    /**
     * Fetches posts from the server and saves them to the local database.
     *
     * Logic:
     * 1. Checks if the local database is empty.
     * 2. If empty, fetches data from the API.
     * 3. Inserts the fetched data into the database.
     *
     * This ensures that the app works offline after the initial fetch and uses the database as the single source of truth.
     *
     * @return Unit (This function is suspend and returns when the operation is complete).
     */
    suspend fun getServerPosts() = withContext(Dispatchers.IO) {
        Log.d("Server", "Checking posts in database")
        if (postsDao.getPostsCount() == 0) {
            Log.d("Server", "No posts in database, fetching remote")
            val posts = client.getAllPosts()
            insertPosts(posts)
        }
    }

    /**
     * Helper method to insert posts into the database.
     *
     * @param posts The list of posts to insert.
     */
    private suspend fun insertPosts(posts: List<Post>) = postsDao.insertPosts(posts)

    /**
     * Observes the list of posts from the database.
     *
     * The return type is a [Flow], so the caller will be notified of any changes to the data in the database.
     *
     * @return A Flow emitting the list of [Post]s.
     */
    fun getPostsFromDatabase(): Flow<List<Post>> = postsDao.getAllPosts()

    /**
     * Marks multiple posts as read in the database.
     *
     * @param postIds The list of IDs of the posts to update.
     */
    suspend fun markRead(postIds: List<Long>) = postsDao.markRead(postIds)

    /**
     * Marks a single post as read in the database.
     *
     * @param postId The ID of the post to update.
     */
    suspend fun markRead(postId: Long) = postsDao.markRead(postId)

    /**
     * Deletes multiple posts from the database.
     *
     * @param postIds The list of IDs of the posts to delete.
     */
    suspend fun deletePosts(postIds: List<Long>) = postsDao.deletePosts(postIds)

    /**
     * Deletes a single post from the database.
     *
     * @param postId The ID of the post to delete.
     */
    suspend fun deletePost(postId: Long) = postsDao.deletePost(postId)

    /**
     * Retrieves a single post by its ID from the database.
     *
     * @param postId The ID of the post to retrieve.
     * @return The [Post] object.
     */
    suspend fun postById(postId: Long) = postsDao.postById(postId)
}
