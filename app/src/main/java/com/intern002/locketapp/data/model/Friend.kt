package com.intern002.locketapp.data.model

data class Friend(
    val id: String,
    val email: String?,
    val username: String,
    val discriminator: Int,
    val birthday: String,
    val avatarUrl: String?
)
