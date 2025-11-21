package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import javax.inject.Inject

class UserApi @Inject constructor(
    private val client: HttpClient
) {
    private val baseUrl = BuildConfig.BASE_URL

    suspend fun logout() {
        client.post("$baseUrl/users/logout")
    }
}
