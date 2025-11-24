package com.example.primarydetailkotlin.posts.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.primarydetailkotlin.posts.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for accessing and managing post data in the local database.
 *
 * This interface defines methods for reading, inserting, updating, and deleting posts using Room.
 */
@Dao
interface PostsDao {

    /**
     * Retrieves all posts from the database, ordered by ID in descending order.
     *
     * The return type is a [Flow], which emits updates whenever the data in the table changes.
     *
     * @return A [Flow] emitting a list of [Post] objects.
     */
    @Query("SELECT * FROM " + Post.TABLE_NAME + " ORDER BY " + Post.COLUMN_ID + " desc")
    fun getAllPosts(): Flow<List<Post>>

    /**
     * Calculates the total number of posts stored in the database.
     *
     * @return The count of posts.
     */
    @Query("SELECT COUNT(*) FROM " + Post.TABLE_NAME)
    fun getPostsCount(): Int

    /**
     * Inserts a list of posts into the database.
     *
     * If a post with the same primary key already exists, it will be replaced due to
     * [OnConflictStrategy.REPLACE].
     *
     * @param posts The list of [Post] objects to insert.
     * @return A list of row IDs for the inserted items.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>): List<Long>

    /**
     * Marks a list of posts as read in the database.
     *
     * @param postIds The list of IDs of the posts to be marked as read.
     */
    @Query("UPDATE " + Post.TABLE_NAME + " SET " + Post.COLUMN_READ + " = 1 WHERE " + Post.COLUMN_ID + " IN (:postIds)")
    suspend fun markRead(postIds: List<Long>)

    /**
     * Marks a single post as read in the database.
     *
     * @param postId The ID of the post to be marked as read.
     */
    @Query("UPDATE " + Post.TABLE_NAME + " SET " + Post.COLUMN_READ + " = 1 WHERE " + Post.COLUMN_ID + " = :postId")
    suspend fun markRead(postId: Long)

    /**
     * Deletes multiple posts from the database based on their IDs.
     *
     * @param postIds The list of IDs of the posts to delete.
     */
    @Query("DELETE FROM " + Post.TABLE_NAME + " WHERE " + Post.COLUMN_ID + " IN (:postIds)")
    suspend fun deletePosts(postIds: List<Long>)

    /**
     * Deletes a single post from the database based on its ID.
     *
     * @param postId The ID of the post to delete.
     */
    @Query("DELETE FROM " + Post.TABLE_NAME + " WHERE " + Post.COLUMN_ID + " = :postId")
    suspend fun deletePost(postId: Long)

    /**
     * Retrieves a single post from the database by its ID.
     *
     * @param postId The ID of the post to retrieve.
     * @return The [Post] object, or null if not found (though return type implies non-nullable, Room throws or returns null depending on config).
     */
    @Query("SELECT * FROM " + Post.TABLE_NAME + " WHERE " + Post.COLUMN_ID + " = :postId")
    suspend fun postById(postId: Long): Post
}
