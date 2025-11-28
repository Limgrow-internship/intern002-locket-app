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
        if (BuildConfig.CLOUDINARY_URL.isNotBlank()) {
            val config = mapOf(
                "cloudinary_url" to BuildConfig.CLOUDINARY_URL
            )
            MediaManager.init(context, config)
        }
        return MediaManager.get()
    }
}
