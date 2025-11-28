package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.data.remote.api.FriendRequestDTO
import com.intern002.locketapp.data.remote.api.FriendshipApi
import com.intern002.locketapp.data.remote.dto.FriendDTO
import com.intern002.locketapp.ui.viewmodel.friends.FriendshipStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

interface FriendshipRepository {
    suspend fun getFriends(): List<Friend>
    suspend fun searchUser(username: String, discriminator: Int): Friend
    suspend fun sendFriendRequest(username: String, discriminator: Int)
    suspend fun getPendingRequests(): List<Friend>
    suspend fun getSentRequests(): List<Friend>
}

class FriendshipRepositoryImpl @Inject constructor(
    private val api: FriendshipApi
) : FriendshipRepository {

    private fun FriendDTO.toFriendWithStatus(): Friend {
        val friendshipStatus = when (status) {
            "accepted" -> FriendshipStatus.FRIEND
            "pending" -> FriendshipStatus.PENDING_INCOMING
            else -> FriendshipStatus.NOT_FRIEND
        }
        return Friend(
            id = id,
            username = username,
            discriminator = discriminator,
            avatarUrl = avatarUrl,
            status = friendshipStatus
        )
    }

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

        val finalStatus = when {
            friends.any { it.id == foundUserDto.id } -> FriendshipStatus.FRIEND
            pendingRequests.any { it.id == foundUserDto.id } -> FriendshipStatus.PENDING_INCOMING
            sentRequests.any { it.id == foundUserDto.id } -> FriendshipStatus.PENDING_OUTGOING
            else -> FriendshipStatus.NOT_FRIEND
        }

        return@coroutineScope Friend(
            id = foundUserDto.id,
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

    override suspend fun getPendingRequests(): List<Friend> {
        return api.getPendingRequests().map { pendingRequest ->
            pendingRequest.requester.toFriendWithStatus().apply {
                status = FriendshipStatus.PENDING_INCOMING
            }
        }
    }

    override suspend fun getSentRequests(): List<Friend> {
        return api.getSentRequests().map { sentRequest ->
            sentRequest.addressee.toFriendWithStatus().apply {
                status = FriendshipStatus.PENDING_OUTGOING
            }
        }
    }
}
