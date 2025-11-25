package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.model.UserProfile

interface UserRepository {
    suspend fun getCurrentUserProfile(): UserProfile?
}
