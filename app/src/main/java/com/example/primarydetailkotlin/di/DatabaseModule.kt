package com.example.primarydetailkotlin.di

import android.content.Context
import androidx.room.Room
import com.example.primarydetailkotlin.posts.services.PostsDao
import com.example.primarydetailkotlin.posts.services.PostsDatabase
import com.example.primarydetailkotlin.util.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies.
 *
 * This module is installed in the [SingletonComponent], meaning dependencies provided here
 * are singletons and live for the entire lifecycle of the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the singleton instance of the [PostsDatabase].
     *
     * It uses [Room.databaseBuilder] to create the database instance.
     *
     * @param appContext The application context.
     * @return The singleton [PostsDatabase] instance.
     */
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): PostsDatabase {
        return Room.databaseBuilder(appContext, PostsDatabase::class.java, DATABASE_NAME).build()
    }

    /**
     * Provides the [PostsDao] instance.
     *
     * The DAO is retrieved from the [PostsDatabase] instance.
     *
     * @param postsDatabase The existing [PostsDatabase] instance.
     * @return The [PostsDao] instance.
     */
    @Singleton
    @Provides
    fun provideDao(postsDatabase: PostsDatabase): PostsDao {
        return postsDatabase.postsDao()
    }
}
