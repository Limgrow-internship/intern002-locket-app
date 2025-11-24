package com.intern002.locketapp.data.model

enum class FriendStatus { NOT_FRIEND, INVITED, FRIEND }

data class Suggestion(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String? = null,
    var status: FriendStatus
)
