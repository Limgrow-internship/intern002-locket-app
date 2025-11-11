package com.intern002.locketapp.di

import com.intern002.locketapp.data.remote.api.LanguageApiService
import com.intern002.locketapp.data.repository.LanguageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://restcountries.com/v3.1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLanguageApiService(retrofit: Retrofit): LanguageApiService {
        return retrofit.create(LanguageApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLanguageRepository(api: LanguageApiService): LanguageRepository {
        return LanguageRepository(api)
    }
}
