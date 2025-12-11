package com.intern002.locketapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.intern002.locketapp.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConversations(conversations: List<ConversationEntity>)

    @Query("SELECT * FROM conversations ORDER BY lastMessageTimestamp DESC")
    fun getConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: String): ConversationEntity?

    @Query("DELETE FROM conversations WHERE partnerId = :partnerId")
    suspend fun deleteConversationByPartnerId(partnerId: String)

    @Query("DELETE FROM conversations")
    suspend fun clearAll()

    @Query("SELECT partnerId FROM conversations WHERE id = :conversationId")
    suspend fun getPartnerIdByConversationId(conversationId: String): String?

}
