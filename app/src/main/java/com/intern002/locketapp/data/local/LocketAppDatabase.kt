package com.intern002.locketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.intern002.locketapp.data.local.dao.ConversationDao
import com.intern002.locketapp.data.local.dao.MessageDao
import com.intern002.locketapp.data.local.dao.PostDao
import com.intern002.locketapp.data.local.entity.ConversationEntity
import com.intern002.locketapp.data.local.entity.MessageEntity
import com.intern002.locketapp.data.local.entity.PostEntity

@Database(
    entities = [ConversationEntity::class, MessageEntity::class, PostEntity::class],
    version = 6,
    exportSchema = false
)
abstract class LocketAppDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao

    abstract fun postDao(): PostDao

}
