package com.intern002.locketapp.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // Add network-related dependencies here (e.g., Retrofit, OkHttpClient)
}
