package com.intern002.locketapp.di

import android.content.Context
import com.cloudinary.android.MediaManager
import com.intern002.locketapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudinaryModule {

    @Provides
    @Singleton
    fun provideMediaManager(@ApplicationContext context: Context): MediaManager {
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_URL.substringAfter("@"),
            "api_key" to BuildConfig.CLOUDINARY_URL.substringAfter("//").substringBefore(":"),
            "api_secret" to BuildConfig.CLOUDINARY_URL.substringAfter(":").substringBefore("@")
        )
        // Step 1: Initialize the MediaManager (this returns Unit)
        MediaManager.init(context, config)
        // Step 2: Get the initialized instance and return it
        return MediaManager.get()
    }
}
