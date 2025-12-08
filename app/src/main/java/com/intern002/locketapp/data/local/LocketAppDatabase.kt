package com.intern002.locketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ConversationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LocketAppDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao

}
