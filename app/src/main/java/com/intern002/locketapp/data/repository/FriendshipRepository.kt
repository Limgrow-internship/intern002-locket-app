package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.data.remote.api.FriendRequestDTO
import com.intern002.locketapp.data.remote.api.FriendshipApi
import com.intern002.locketapp.ui.viewmodel.friends.FriendshipStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

interface FriendshipRepository {
    suspend fun getFriends(): List<Friend>
    suspend fun searchUser(username: String, discriminator: Int): Friend
    suspend fun sendFriendRequest(username: String, discriminator: Int)
    suspend fun acceptFriendRequest(friendshipId: String)
    suspend fun rejectFriendRequest(friendshipId: String)
    suspend fun deleteFriendship(friendshipId: String)
    suspend fun getPendingRequests(): List<Friend>
    suspend fun getSentRequests(): List<Friend>
    suspend fun getSuggestions(): List<Friend>
}

class FriendshipRepositoryImpl @Inject constructor(
    private val api: FriendshipApi
) : FriendshipRepository {

    override suspend fun getFriends(): List<Friend> {
        return api.getFriends().map { friendshipDto ->
            val user = friendshipDto.user
            Friend(
                id = user.id,
                username = user.username,
                discriminator = user.discriminator,
                avatarUrl = user.avatarUrl,
                status = FriendshipStatus.FRIEND
            )
        }
    }

    override suspend fun searchUser(username: String, discriminator: Int): Friend = coroutineScope {
        val foundUserDto = api.searchUser(username, discriminator)

        val friendsDeferred = async { getFriends() }
        val pendingRequestsDeferred = async { getPendingRequests() }
        val sentRequestsDeferred = async { getSentRequests() }

        val friends = friendsDeferred.await()
        val pendingRequests = pendingRequestsDeferred.await()
        val sentRequests = sentRequestsDeferred.await()

        val existingFriend = friends.find { it.username == foundUserDto.username && it.discriminator == foundUserDto.discriminator }
        val existingPending = pendingRequests.find { it.username == foundUserDto.username && it.discriminator == foundUserDto.discriminator }
        val existingSent = sentRequests.find { it.username == foundUserDto.username && it.discriminator == foundUserDto.discriminator }

        val finalStatus: FriendshipStatus
        val finalId: String

        when {
            existingFriend != null -> {
                finalStatus = FriendshipStatus.FRIEND
                finalId = existingFriend.id
            }
            existingPending != null -> {
                finalStatus = FriendshipStatus.PENDING_INCOMING
                finalId = existingPending.id
            }
            existingSent != null -> {
                finalStatus = FriendshipStatus.PENDING_OUTGOING
                finalId = existingSent.id
            }
            else -> {
                finalStatus = FriendshipStatus.NOT_FRIEND
                finalId = foundUserDto.id
            }
        }

        return@coroutineScope Friend(
            id = finalId, 
            username = foundUserDto.username,
            discriminator = foundUserDto.discriminator,
            avatarUrl = foundUserDto.avatarUrl,
            status = finalStatus
        )
    }

    override suspend fun sendFriendRequest(username: String, discriminator: Int) {
        val request = FriendRequestDTO(username, discriminator)
        api.sendFriendRequest(request)
    }

    override suspend fun acceptFriendRequest(friendshipId: String) {
        api.acceptFriendRequest(friendshipId)
    }

    override suspend fun rejectFriendRequest(friendshipId: String) {
        api.rejectFriendRequest(friendshipId)
    }

    override suspend fun deleteFriendship(friendshipId: String) {
        api.deleteFriendship(friendshipId)
    }

    override suspend fun getPendingRequests(): List<Friend> {
        return api.getPendingRequests().map { pendingRequest ->
            val userDto = pendingRequest.requester
            Friend(
                id = pendingRequest.friendshipId,
                username = userDto.username,
                discriminator = userDto.discriminator,
                avatarUrl = userDto.avatarUrl,
                status = FriendshipStatus.PENDING_INCOMING
            )
        }
    }

    override suspend fun getSentRequests(): List<Friend> {
        return api.getSentRequests().map { sentRequest ->
            val userDto = sentRequest.addressee
            Friend(
                id = sentRequest.friendshipId, 
                username = userDto.username,
                discriminator = userDto.discriminator,
                avatarUrl = userDto.avatarUrl,
                status = FriendshipStatus.PENDING_OUTGOING
            )
        }
    }

    override suspend fun getSuggestions(): List<Friend> {
        return api.getSuggestions().map { friendDto ->
            Friend(
                id = friendDto.id,
                username = friendDto.username,
                discriminator = friendDto.discriminator,
                avatarUrl = friendDto.avatarUrl,
                status = FriendshipStatus.NOT_FRIEND
            )
        }
    }
}
