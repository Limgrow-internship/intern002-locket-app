package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.api.FriendApi
import com.intern002.locketapp.data.remote.api.FriendRequestDto
import com.intern002.locketapp.data.remote.model.friend.FriendUserResponse
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val friendApi: FriendApi
) : FriendRepository {

    override suspend fun getFriends(): Result<List<FriendUserResponse>> {
        return try {
            val response = friendApi.getFriends()
            if (response.status.isSuccess()) {
                val friends = response.body<List<FriendUserResponse>>() ?: emptyList()
                Result.success(friends)
            } else {
                Result.failure(Exception("Lỗi lấy danh sách bạn: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchUser(
        username: String,
        discriminator: Int
    ): Result<FriendUserResponse> {
        return try {
            val response = friendApi.searchUser(username, discriminator)
            if (response.status.isSuccess() && response.body<List<FriendUserResponse>>() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Không tìm thấy user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendFriendRequest(username: String, discriminator: Int): Result<Unit> {
        return try {
            val response = friendApi.sendFriendRequest(FriendRequestDto(username, discriminator))
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Gửi thất bại: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}