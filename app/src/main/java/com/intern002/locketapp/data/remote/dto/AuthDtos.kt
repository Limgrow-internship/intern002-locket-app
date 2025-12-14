package com.intern002.locketapp.data.remote.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)