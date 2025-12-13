package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.data.remote.dto.UpdateUserRequest
import com.intern002.locketapp.data.model.UserProfile
import com.intern002.locketapp.data.remote.api.UserApi
import com.intern002.locketapp.data.remote.dto.VerifyPasswordRequest
import com.intern002.locketapp.di.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun verifyPassword(password: String): Boolean
    suspend fun getCurrentUserProfile(): UserProfile
    suspend fun updateEmail(newEmail: String)
    suspend fun updateUsername(newUsername: String)
    suspend fun updateBirthday(birthday: String)
    suspend fun updateAvatar(avatarUrl: String?)
    suspend fun deleteAvatar()
    fun clearCurrentUserProfile()
    suspend fun logout()
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val chatRepository: ChatRepository,
    private val sessionManager: SessionManager,
    private val authManager: AuthManager
) : UserRepository {

    private var cachedProfile: UserProfile? = null

    override suspend fun verifyPassword(password: String): Boolean {
        val request = VerifyPasswordRequest(password = password)
        val response = userApi.verifyPassword(request)
        return response.isCorrect
    }

    override suspend fun getCurrentUserProfile(): UserProfile {
        cachedProfile?.let { return it }
        return userApi.getProfile().also {
            cachedProfile = it
        }
    }

    override suspend fun updateEmail(newEmail: String) {
        val request = UpdateUserRequest(email = newEmail)
        userApi.updateUser(request)
        clearCurrentUserProfile()
    }

    override suspend fun updateUsername(newUsername: String) {
        val request = UpdateUserRequest(username = newUsername)
        userApi.updateUser(request)
        clearCurrentUserProfile()
    }

    override suspend fun updateBirthday(birthday: String) {
        val request = UpdateUserRequest(birthday = birthday)
        userApi.updateUser(request)
        clearCurrentUserProfile()
    }

    override suspend fun updateAvatar(avatarUrl: String?) {
        val request = UpdateUserRequest(avatarUrl = avatarUrl)
        userApi.updateUser(request)
        clearCurrentUserProfile()
    }

    override suspend fun deleteAvatar() {
        userApi.deleteAvatar()
        clearCurrentUserProfile()
    }

    override fun clearCurrentUserProfile() {
        cachedProfile = null
    }

    override suspend fun logout() {
        sessionManager.clearSession()
        authManager.clearTokens()
        chatRepository.clearAllLocalData()
        clearCurrentUserProfile()
    }
}