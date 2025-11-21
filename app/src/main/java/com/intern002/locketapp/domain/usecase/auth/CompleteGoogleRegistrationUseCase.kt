package com.intern002.locketapp.domain.usecase.auth

import com.intern002.locketapp.data.remote.response.AuthResponse
import com.intern002.locketapp.data.repository.AuthRepository
import com.intern002.locketapp.utils.Result
import javax.inject.Inject

class CompleteGoogleRegistrationUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String, username: String, birthday: String): Result<AuthResponse> {
        return repository.completeGoogleRegistration(idToken, username, birthday)
    }
}
