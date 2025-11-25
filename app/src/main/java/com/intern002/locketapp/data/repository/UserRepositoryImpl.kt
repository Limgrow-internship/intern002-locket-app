package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.model.UserProfile
import com.intern002.locketapp.data.remote.api.UserApi // Updated import path
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getCurrentUserProfile(): UserProfile? {
        return try {
            userApi.getProfile()
        } catch (e: Exception) {
            // You should handle exceptions more gracefully, e.g., by logging them
            null
        }
    }
}
