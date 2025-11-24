package com.example.primarydetailkotlin.posts.services

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.primarydetailkotlin.posts.domain.model.Post

/**
 * The Room Database class for the application.
 *
 * This class defines the database configuration and serves as the main access point
 * to the persisted data. It exposes the DAOs (Data Access Objects) for interacting with the tables.
 *
 * @property entities The list of entities (tables) included in the database.
 * @property version The current version of the database schema.
 * @property exportSchema Whether to export the schema to a folder (disabled here).
 */
@Database(entities = [Post::class], version = 1, exportSchema = false)
abstract class PostsDatabase : RoomDatabase() {

    /**
     * Provides access to the [PostsDao].
     *
     * @return The [PostsDao] instance for accessing post-related data.
     */
    abstract fun postsDao(): PostsDao

}
