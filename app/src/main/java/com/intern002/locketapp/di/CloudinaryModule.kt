package com.intern002.locketapp.di

import com.intern002.locketapp.data.repository.CloudinaryRepository
import com.intern002.locketapp.data.repository.CloudinaryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CloudinaryModule {

    @Binds
    @Singleton
    abstract fun bindCloudinaryRepository(
        impl: CloudinaryRepositoryImpl
    ): CloudinaryRepository
}