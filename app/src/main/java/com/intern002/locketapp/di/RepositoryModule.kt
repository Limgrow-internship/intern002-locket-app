package com.intern002.locketapp.di

import com.intern002.locketapp.data.remote.api.FcmApi
import com.intern002.locketapp.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFriendshipRepository(impl: FriendshipRepositoryImpl): FriendshipRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): PostRepository
}

@Module
@InstallIn(SingletonComponent::class)
object FcmRepositoryModule {
    @Provides
    @Singleton
    fun provideFcmRepository(fcmApi: FcmApi): FcmRepository {
        return FcmRepository(fcmApi)
    }
}
