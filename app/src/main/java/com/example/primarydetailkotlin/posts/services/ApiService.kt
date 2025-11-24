package com.example.primarydetailkotlin.posts.services

import com.example.primarydetailkotlin.posts.domain.model.Post
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit interface defining the API endpoints for fetching posts.
 *
 * This interface is used by Retrofit to generate the network implementation.
 */
interface ApiService {

    /**
     * Retrieves a list of all posts from the server.
     *
     * @return A list of [Post] objects.
     */
    @GET("/posts")
    suspend fun getAllPosts(): List<Post>

    /**
     * Retrieves a single post by its unique identifier.
     *
     * @param postId The ID of the post to retrieve.
     * @return The [Post] object matching the ID.
     */
    @GET("/posts/{id}")
    suspend fun getPostById(@Path(value = "id") postId: Int): Post
}
