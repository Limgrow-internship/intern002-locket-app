package com.intern002.locketapp.data.remote.model.auth

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.OptIn

@OptIn(InternalSerializationApi::class)
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
