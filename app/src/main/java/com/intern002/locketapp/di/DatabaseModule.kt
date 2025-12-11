package com.intern002.locketapp.di

import android.content.Context
import androidx.room.Room
import com.intern002.locketapp.data.local.dao.ConversationDao
import com.intern002.locketapp.data.local.LocketAppDatabase
import com.intern002.locketapp.data.local.dao.MessageDao
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
    fun provideLocketAppDatabase(
        @ApplicationContext context: Context
    ): LocketAppDatabase {
        return Room.databaseBuilder(
            context,
            LocketAppDatabase::class.java,
            "locket_app.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideConversationDao(database: LocketAppDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: LocketAppDatabase): MessageDao {
        return database.messageDao()
    }


    @Provides
    @Singleton
    fun providePostDao(database: LocketAppDatabase): PostDao {
        return database.postDao()
    }
}
