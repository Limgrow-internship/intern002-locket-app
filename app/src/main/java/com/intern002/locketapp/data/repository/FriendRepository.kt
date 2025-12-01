package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.model.friend.FriendUserResponse

interface FriendRepository {
    suspend fun getFriends(): Result<List<FriendUserResponse>>
    suspend fun searchUser(username: String, discriminator: Int): Result<FriendUserResponse>
    suspend fun sendFriendRequest(username: String, discriminator: Int): Result<Unit>
}