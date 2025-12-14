package com.intern002.locketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.intern002.locketapp.data.local.dao.PostDao
import com.intern002.locketapp.data.local.entity.Converters
import com.intern002.locketapp.data.local.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}