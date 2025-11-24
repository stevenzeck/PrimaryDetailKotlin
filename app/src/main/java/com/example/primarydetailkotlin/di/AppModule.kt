package com.example.primarydetailkotlin.di

import com.example.primarydetailkotlin.posts.services.ApiService
import com.example.primarydetailkotlin.posts.services.PostsDao
import com.example.primarydetailkotlin.posts.ui.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module for providing application-level dependencies.
 *
 * This module is installed in the [ViewModelComponent], meaning dependencies provided here
 * will live as long as the ViewModel they are injected into.
 */
@Module
@InstallIn(ViewModelComponent::class)
object AppModule {

    /**
     * Provides an instance of [PostRepository].
     *
     * @param client The [ApiService] used for network operations.
     * @param postsDao The [PostsDao] used for database operations.
     * @return A new instance of [PostRepository].
     */
    @Provides
    @ViewModelScoped
    fun provideRepository(
        client: ApiService,
        postsDao: PostsDao
    ) = PostRepository(client, postsDao)

}
