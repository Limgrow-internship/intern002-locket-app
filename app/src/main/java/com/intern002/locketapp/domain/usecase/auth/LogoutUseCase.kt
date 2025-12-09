package com.intern002.locketapp.domain.usecase.auth

import com.intern002.locketapp.data.repository.AuthRepository
import com.intern002.locketapp.data.repository.FriendshipRepository
import com.intern002.locketapp.data.repository.UserRepository
import com.intern002.locketapp.di.SessionManager
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke() {
        authRepository.logout() // Xóa token trong AuthManager
        userRepository.clearCurrentUserProfile() // Xóa cache trong UserRepository
        friendshipRepository.clearCache() // Xóa cache trong FriendshipRepository
        sessionManager.clearSession() // Hủy HttpClient cũ
    }
}
