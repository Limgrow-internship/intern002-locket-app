package com.intern002.locketapp.data.model

data class Message(
    val id: String,
    val content: String,
    val senderId: String, // ID of the user who sent the message
    val timestamp: Long, // Use Long for easier sorting
    val imageUrl: String? = null // For special image messages
)
