package com.intern002.locketapp.di

import com.intern002.locketapp.data.repository.FriendshipRepository
import com.intern002.locketapp.data.repository.FriendshipRepositoryImpl
import com.intern002.locketapp.data.repository.UserRepository
import com.intern002.locketapp.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
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

}
