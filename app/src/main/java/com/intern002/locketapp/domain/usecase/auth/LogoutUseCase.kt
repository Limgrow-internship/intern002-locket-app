package com.intern002.locketapp.domain.usecase.auth

import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.data.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val authManager: AuthManager
) {
    suspend operator fun invoke() {
        repository.logout()
        authManager.clearTokens()
    }
}
