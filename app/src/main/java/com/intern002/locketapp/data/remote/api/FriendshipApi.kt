package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.remote.dto.FriendDTO
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import javax.inject.Inject

class FriendshipApi @Inject constructor(private val client: HttpClient) {
    private val baseUrl = BuildConfig.BASE_URL

    suspend fun getFriends(): List<FriendDTO> {
        return client.get("$baseUrl/friends").body()
    }

    suspend fun searchUser(username: String, discriminator: Int): FriendDTO {
        return client.get("$baseUrl/friends/search") {
            parameter("username", username)
            parameter("discriminator", discriminator)
        }.body()
    }
}
