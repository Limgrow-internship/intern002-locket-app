package com.intern002.locketapp.data.remote.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class ConversationPartnerDTO(
    val id: String,
    val username: String,
    val avatarUrl: String?
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class MessageDTO(
    val senderId: String,
    val messageType: String,
    val content: String?,
    val imageUrl: String?,
    val createdAt: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class ConversationListItemDTO(
    val conversationId: String,
    val partner: ConversationPartnerDTO,
    val lastMessage: MessageDTO?,
    val createdAt: String
)
