// AppModule.kt
package com.intern002.locketapp.di

import com.intern002.locketapp.data.repository.LanguageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideLanguageRepository(): LanguageRepository {
        return LanguageRepository()
    }
}