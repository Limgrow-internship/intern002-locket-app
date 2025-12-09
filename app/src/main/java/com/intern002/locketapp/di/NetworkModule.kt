package com.intern002.locketapp.di

import com.intern002.locketapp.data.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthApi(sessionManager: SessionManager): AuthApi {
        return AuthApi(sessionManager)
    }

    @Provides
    @Singleton
    fun provideUserApi(sessionManager: SessionManager): UserApi {
        return UserApi(sessionManager)
    }

    @Provides
    @Singleton
    fun provideChatApi(sessionManager: SessionManager): ChatApi {
        return ChatApi(sessionManager)
    }

    @Provides
    @Singleton
    fun providePostApi(sessionManager: SessionManager): PostApi {
        return PostApi(sessionManager)
    }

    @Provides
    @Singleton
    fun provideFcmApi(sessionManager: SessionManager): FcmApi {
        return FcmApi(sessionManager)
    }

    @Provides
    @Singleton
    fun provideFriendshipApi(sessionManager: SessionManager): FriendshipApi {
        return FriendshipApi(sessionManager)
    }

    @Provides
    @Singleton
    fun provideReactionApi(sessionManager: SessionManager): ReactionApi {
        return ReactionApi(sessionManager)
    }
}