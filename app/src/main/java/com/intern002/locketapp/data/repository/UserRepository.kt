package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.dto.UpdateUserRequest
import com.intern002.locketapp.data.model.UserProfile
import com.intern002.locketapp.data.remote.api.UserApi
import com.intern002.locketapp.data.remote.dto.VerifyPasswordRequest
import javax.inject.Inject

interface UserRepository {
    suspend fun verifyPassword(password: String): Boolean
    suspend fun getCurrentUserProfile(): UserProfile
    suspend fun updateEmail(newEmail: String)
    suspend fun updateUsername(newUsername: String)
    suspend fun updateBirthday(birthday: String)
}

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun verifyPassword(password: String): Boolean {
        val request = VerifyPasswordRequest(password = password)
        val response = userApi.verifyPassword(request)
        return response.isCorrect
    }

    override suspend fun getCurrentUserProfile(): UserProfile {
        return userApi.getProfile()
    }

    override suspend fun updateEmail(newEmail: String) {
        val request = UpdateUserRequest(email = newEmail)
        userApi.updateUser(request)
    }

    override suspend fun updateUsername(newUsername: String) {
        val request = UpdateUserRequest(username = newUsername)
        userApi.updateUser(request)
    }

    override suspend fun updateBirthday(birthday: String) {
        val request = UpdateUserRequest(birthday = birthday)
        userApi.updateUser(request)
    }
}