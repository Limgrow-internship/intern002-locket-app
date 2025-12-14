package com.intern002.locketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.intern002.locketapp.data.local.dao.PostDao
import com.intern002.locketapp.data.local.entity.PostEntity

@Database(
    entities = [ConversationEntity::class, MessageEntity::class, PostEntity::class],
    version = 4,
    exportSchema = false
)
abstract class LocketAppDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao

    abstract fun postDao(): PostDao

}
