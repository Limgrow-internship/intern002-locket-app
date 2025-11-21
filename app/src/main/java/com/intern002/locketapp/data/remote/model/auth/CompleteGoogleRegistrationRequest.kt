package com.intern002.locketapp.data.remote.model.auth

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class CompleteGoogleRegistrationRequest(
    val idToken: String,
    val username: String,
    val birthday: String
)
