@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.intern002.locketapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val username: String,
    val discriminator: Int,
    val avatarUrl: String?,
    val birthday: String
)
