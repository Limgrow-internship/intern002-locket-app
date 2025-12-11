package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.remote.dto.FriendDTO
import com.intern002.locketapp.data.remote.dto.FriendshipDTO
import com.intern002.locketapp.di.SessionManager
import io.ktor.client.* 
import io.ktor.client.call.body
import io.ktor.client.request.* 
import io.ktor.http.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import javax.inject.Inject

@OptIn(InternalSerializationApi::class)
@Serializable
data class FriendRequestDTO(val username: String, val discriminator: Int)

@OptIn(InternalSerializationApi::class)
@Serializable
data class PendingRequestDTO(
    val friendshipId: String,
    val requester: FriendDTO,
    val status: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class SentRequestDTO(
    val friendshipId: String,
    val addressee: FriendDTO,
    val status: String
)


class FriendshipApi @Inject constructor(private val sessionManager: SessionManager) {
    private val baseUrl = BuildConfig.BASE_URL

    private fun client() = sessionManager.getClient()

    suspend fun getFriends(): List<FriendshipDTO> {
        return client().get("$baseUrl/friends").body()
    }

    suspend fun getBlockedFriends(): List<FriendDTO> {
        return client().get("$baseUrl/friends/blocked").body()
    }

    suspend fun searchUser(username: String, discriminator: Int): FriendDTO {
        return client().get("$baseUrl/friends/search") {
            parameter("username", username)
            parameter("discriminator", discriminator)
        }.body()
    }

    suspend fun sendFriendRequest(request: FriendRequestDTO) {
        client().post("$baseUrl/friends/requests") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun rejectFriendRequest(friendshipId: String) {
        client().put("$baseUrl/friends/reject/$friendshipId")
    }
    
    suspend fun acceptFriendRequest(friendshipId: String) {
        client().put("$baseUrl/friends/accept/$friendshipId")
    }

    suspend fun deleteFriendship(friendshipId: String) {
        client().delete("$baseUrl/friends/$friendshipId")
    }

    suspend fun blockFriend(friendId: String) {
        client().post("$baseUrl/friends/block/$friendId")
    }

    suspend fun unblockFriend(friendId: String) {
        client().post("$baseUrl/friends/unblock/$friendId")
    }

    suspend fun getPendingRequests(): List<PendingRequestDTO> {
        return client().get("$baseUrl/friends/requests/pending").body()
    }

    suspend fun getSentRequests(): List<SentRequestDTO> {
        return client().get("$baseUrl/friends/requests/sent").body()
    }

    suspend fun getSuggestions(): List<FriendDTO> {
        return client().get("$baseUrl/friends/suggestions").body()
    }
}
