package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.remote.dto.UpdateUserRequest
import com.intern002.locketapp.data.model.UserProfile
import com.intern002.locketapp.data.remote.dto.VerifyPasswordRequest
import com.intern002.locketapp.data.remote.response.VerifyPasswordResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class UserApi @Inject constructor(
    private val client: HttpClient
) {
    private val baseUrl = BuildConfig.BASE_URL

    suspend fun getProfile(): UserProfile {
        return client.get("$baseUrl/users/me").body()
    }

    suspend fun updateUser(request: UpdateUserRequest): UserProfile {
        return client.put("$baseUrl/users/update") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun verifyPassword(request: VerifyPasswordRequest): VerifyPasswordResponse {
        return client.post("$baseUrl/users/verify-password") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun logout() {
        client.post("$baseUrl/users/logout")
    }
}