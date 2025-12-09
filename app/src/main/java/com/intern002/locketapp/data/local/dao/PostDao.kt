package com.intern002.locketapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.intern002.locketapp.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnePost(post: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun clearAll()

    @Transaction
    suspend fun refreshPosts(posts: List<PostEntity>) {
        clearAll()
        insertPosts(posts)
    }
}