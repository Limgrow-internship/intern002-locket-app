package com.intern002.locketapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class FriendApi @Inject constructor(
    private val client: HttpClient
) {


    suspend fun getFriends(): HttpResponse {
        return client.get("friends")
    }

    suspend fun searchUser(username: String, discriminator: Int): HttpResponse {
        return client.get("friends/search") {
            parameter("username", username)
            parameter("discriminator", discriminator)
        }
    }

    suspend fun sendFriendRequest(request: FriendRequestDto): HttpResponse {
        return client.post("friends/requests") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun getPendingRequests(): HttpResponse {
        return client.get("friends/requests/pending")
    }

}

data class FriendRequestDto(val username: String, val discriminator: Int)