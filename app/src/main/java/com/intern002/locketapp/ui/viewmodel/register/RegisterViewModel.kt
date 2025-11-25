package com.intern002.locketapp.ui.viewmodel.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.domain.usecase.auth.CompleteGoogleRegistrationUseCase
import com.intern002.locketapp.domain.usecase.auth.RegisterUseCase
import com.intern002.locketapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val completeGoogleRegistrationUseCase: CompleteGoogleRegistrationUseCase,
    private val authManager: AuthManager
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun onRegisterClicked(email: String, username: String, password: String, birthday: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            when (val result = registerUseCase(email, username, password, birthday)) {
                is Result.Success -> {
                    result.data?.let {
                        authManager.saveTokens(it.accessToken, it.refreshToken)
                    }
                    _registerState.value = RegisterState.Success("Registration successful!")
                }
                is Result.Error -> {
                    Log.d("RegisterViewModel", "Registration failed: ${result.message}")
                    _registerState.value = RegisterState.Error(result.message ?: "An unknown error occurred")
                }
                is Result.Loading -> {
                    // Already handled by setting the state before the call, do nothing.
                }
                else -> {
                    _registerState.value = RegisterState.Error("An unexpected error occurred")
                }
            }
        }
    }

    fun onCompleteGoogleRegistrationClicked(idToken: String, username: String, birthday: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            when (val result = completeGoogleRegistrationUseCase(idToken, username, birthday)) {
                is Result.Success -> {
                    result.data?.let {
                        authManager.saveTokens(it.accessToken, it.refreshToken)
                    }
                    _registerState.value = RegisterState.Success("Registration successful!")
                }
                is Result.Error -> {
                    Log.d("RegisterViewModel", "Google registration failed: ${result.message}")
                    _registerState.value = RegisterState.Error(result.message ?: "An unknown error occurred")
                }
                else -> {
                    _registerState.value = RegisterState.Error("An unexpected error occurred")
                }
            }
        }
    }
}
