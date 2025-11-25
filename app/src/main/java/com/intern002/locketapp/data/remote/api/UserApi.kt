package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.model.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import javax.inject.Inject

class UserApi @Inject constructor(
    private val client: HttpClient
) {
    private val baseUrl = BuildConfig.BASE_URL

    suspend fun getProfile(): UserProfile {
        return client.get("$baseUrl/users/me").body()
    }

    suspend fun logout() {
        client.post("$baseUrl/users/logout")
    }
}
