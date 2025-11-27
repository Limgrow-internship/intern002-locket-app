package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.data.remote.api.FriendshipApi
import com.intern002.locketapp.data.remote.dto.FriendDTO
import javax.inject.Inject

interface FriendshipRepository {
    suspend fun getFriends(): List<Friend>
    suspend fun searchUser(username: String, discriminator: Int): Friend
}

class FriendshipRepositoryImpl @Inject constructor(
    private val api: FriendshipApi
) : FriendshipRepository {

    private fun FriendDTO.toFriend(): Friend {
        return Friend(
            id = id,
            email = email,
            username = username,
            discriminator = discriminator,
            birthday = birthday,
            avatarUrl = avatarUrl
        )
    }

    override suspend fun getFriends(): List<Friend> {
        return api.getFriends().map { it.toFriend() }
    }

    override suspend fun searchUser(username: String, discriminator: Int): Friend {
        return api.searchUser(username, discriminator).toFriend()
    }
}