package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.data.remote.api.FriendRequestDTO
import com.intern002.locketapp.data.remote.api.FriendshipApi
import com.intern002.locketapp.ui.viewmodel.friends.FriendshipStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

interface FriendshipRepository {
    suspend fun getFriends(): List<Friend>
    suspend fun searchUser(username: String, discriminator: Int): Friend
    suspend fun sendFriendRequest(username: String, discriminator: Int)
    suspend fun acceptFriendRequest(friendshipId: String)
    suspend fun rejectFriendRequest(friendshipId: String)
    suspend fun deleteFriendship(friendshipId: String)
    suspend fun blockFriend(friendId: String)
    suspend fun getPendingRequests(): List<Friend>
    suspend fun getSentRequests(): List<Friend>
    suspend fun getSuggestions(): List<Friend>
    fun clearCache()
}

@Singleton
class FriendshipRepositoryImpl @Inject constructor(
    private val api: FriendshipApi
) : FriendshipRepository {

    private var friendsCache: List<Friend>? = null
    private var pendingRequestsCache: List<Friend>? = null
    private var sentRequestsCache: List<Friend>? = null
    private var suggestionsCache: List<Friend>? = null


    override suspend fun getFriends(): List<Friend> {
        friendsCache?.let { return it }
        return api.getFriends().map {
            val user = it.user
            Friend(
                id = user.id,
                username = user.username,
                discriminator = user.discriminator,
                avatarUrl = user.avatarUrl,
                status = FriendshipStatus.FRIEND
            )
        }.also { friendsCache = it }
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
        clearCache()
    }

    override suspend fun acceptFriendRequest(friendshipId: String) {
        api.acceptFriendRequest(friendshipId)
        clearCache()
    }

    override suspend fun rejectFriendRequest(friendshipId: String) {
        api.rejectFriendRequest(friendshipId)
        clearCache()
    }

    override suspend fun deleteFriendship(friendshipId: String) {
        api.deleteFriendship(friendshipId)
        clearCache()
    }

    override suspend fun blockFriend(friendId: String) {
        api.blockFriend(friendId)
        clearCache()
    }

    override suspend fun getPendingRequests(): List<Friend> {
        pendingRequestsCache?.let { return it }
        return api.getPendingRequests().map { pendingRequest ->
            val userDto = pendingRequest.requester
            Friend(
                id = pendingRequest.friendshipId,
                username = userDto.username,
                discriminator = userDto.discriminator,
                avatarUrl = userDto.avatarUrl,
                status = FriendshipStatus.PENDING_INCOMING
            )
        }.also { pendingRequestsCache = it }
    }

    override suspend fun getSentRequests(): List<Friend> {
        sentRequestsCache?.let { return it }
        return api.getSentRequests().map { sentRequest ->
            val userDto = sentRequest.addressee
            Friend(
                id = sentRequest.friendshipId, 
                username = userDto.username,
                discriminator = userDto.discriminator,
                avatarUrl = userDto.avatarUrl,
                status = FriendshipStatus.PENDING_OUTGOING
            )
        }.also { sentRequestsCache = it }
    }

    override suspend fun getSuggestions(): List<Friend> {
        suggestionsCache?.let { return it }
        return api.getSuggestions().map { friendDto ->
            Friend(
                id = friendDto.id,
                username = friendDto.username,
                discriminator = friendDto.discriminator,
                avatarUrl = friendDto.avatarUrl,
                status = FriendshipStatus.NOT_FRIEND
            )
        }.also { suggestionsCache = it }
    }

    override fun clearCache() {
        friendsCache = null
        pendingRequestsCache = null
        sentRequestsCache = null
        suggestionsCache = null
    }
}
