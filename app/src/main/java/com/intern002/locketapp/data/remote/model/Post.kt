package com.intern002.locketapp.data.remote.model

data class Post(
    val id: String,
    val authorId: String,
    val userName: String?,
    val userAvatarUrl: String?,
    val mediaUrl: String,
    val mediaType: String,
    val caption: String?,
    val createdAt: String,
    val reactors: List<Reactor> = emptyList()
)