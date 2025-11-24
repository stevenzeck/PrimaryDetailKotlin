package com.example.primarydetailkotlin.posts.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the UI state of the Posts feature.
 *
 * This ViewModel handles business logic for fetching, marking as read, and deleting posts.
 * It interacts with the [PostRepository] to perform these operations.
 *
 * @property repository The repository used to access post data.
 */
@HiltViewModel
class PostViewModel @Inject constructor(private val repository: PostRepository) : ViewModel() {

    /**
     * A [kotlinx.coroutines.flow.Flow] of posts retrieved from the database.
     * The UI observes this property to display the list of posts.
     */
    val posts = repository.getPostsFromDatabase()

    /**
     * Triggers a network request to fetch posts from the server.
     *
     * This method launches a coroutine in the [viewModelScope] to perform the network operation asynchronously.
     */
    fun serverPosts() {
        viewModelScope.launch {
            repository.getServerPosts()
        }
    }

    /**
     * Marks a list of posts as read.
     *
     * @param postIds A list of IDs of posts to mark as read.
     */
    fun markRead(postIds: List<Long>) = viewModelScope.launch {
        repository.markRead(postIds)
    }

    /**
     * Marks a single post as read.
     *
     * @param postId The ID of the post to mark as read.
     */
    fun markRead(postId: Long) = viewModelScope.launch {
        repository.markRead(postId)
    }

    /**
     * Deletes a list of posts from the database.
     *
     * @param postIds A list of IDs of posts to delete.
     */
    fun deletePosts(postIds: List<Long>) = viewModelScope.launch {
        repository.deletePosts(postIds)
    }

    /**
     * Deletes a single post from the database.
     *
     * @param postId The ID of the post to delete.
     */
    fun deletePost(postId: Long) = viewModelScope.launch {
        repository.deletePost(postId)
    }

    /**
     * Retrieves a specific post by its ID.
     *
     * @param postId The unique identifier of the post.
     * @return The [com.example.primarydetailkotlin.posts.domain.model.Post] object corresponding to the ID.
     */
    suspend fun postById(postId: Long) = repository.postById(postId)
}
