package com.intern002.locketapp.di

import android.content.Context
import androidx.room.Room
import com.intern002.locketapp.data.local.AppDatabase
import com.intern002.locketapp.data.local.dao.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "locket_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()
}
