package com.example.primarydetailkotlin.posts.services

import com.example.primarydetailkotlin.posts.domain.model.Post
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests the serialization and deserialization of the Post model.
 *
 * Since MockWebServer is not available, we test the core logic of the network layer:
 * ensuring our data model correctly parses the JSON returned by the API.
 * This verifies that @SerialName annotations and field types are correct.
 */
class ApiServiceTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun post_deserialization_isCorrect() {
        val jsonString = """
            {
                "id": 1,
                "userId": 10,
                "title": "Test Title",
                "body": "Test Body"
            }
        """.trimIndent()

        val post = json.decodeFromString<Post>(jsonString)

        assertEquals(1L, post.id)
        assertEquals(10, post.userId)
        assertEquals("Test Title", post.title)
        assertEquals("Test Body", post.body)
        assertEquals(false, post.read) // Default value for local DB, not in JSON
    }

    @Test
    fun postList_deserialization_isCorrect() {
        val jsonString = """
            [
              { "id": 1, "userId": 1, "title": "T1", "body": "B1" },
              { "id": 2, "userId": 1, "title": "T2", "body": "B2" }
            ]
        """.trimIndent()

        val posts = json.decodeFromString<List<Post>>(jsonString)

        assertEquals(2, posts.size)
        assertEquals(1L, posts[0].id)
        assertEquals(2L, posts[1].id)
    }

    @Test
    fun post_deserialization_ignoresExtraFields() {
        // Verifies that if the API adds new fields in the future, the app won't crash.
        val jsonString = """
            {
                "id": 1,
                "userId": 10,
                "title": "T",
                "body": "B",
                "unknown_field": "some data"
            }
        """.trimIndent()

        val post = json.decodeFromString<Post>(jsonString)
        assertEquals(1L, post.id)
    }
}
