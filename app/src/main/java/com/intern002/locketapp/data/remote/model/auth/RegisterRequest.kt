package com.intern002.locketapp.data.remote.model.auth

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.OptIn

@OptIn(InternalSerializationApi::class)
@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val birthday: String
)
