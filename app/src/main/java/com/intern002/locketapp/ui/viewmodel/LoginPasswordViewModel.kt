package com.intern002.locketapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.domain.usecase.auth.LoginUseCase
import com.intern002.locketapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PasswordLoginState {
    object Idle : PasswordLoginState()
    object Loading : PasswordLoginState()
    data class Success(val message: String) : PasswordLoginState()
    data class Error(val message: String) : PasswordLoginState()
    data class NeedsRegistration(val email: String, val pass: String) : PasswordLoginState()
}

@HiltViewModel
class LoginPasswordViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authManager: AuthManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<PasswordLoginState>(PasswordLoginState.Idle)
    val loginState: StateFlow<PasswordLoginState> = _loginState

    fun onContinueClicked(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = PasswordLoginState.Loading
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    result.data?.let {
                        authManager.saveTokens(it.accessToken, it.refreshToken)
                        _loginState.value = PasswordLoginState.Success("Login successful!")
                    } ?: run {
                        _loginState.value = PasswordLoginState.Error("Login failed: Response was empty.")
                    }
                }
                is Result.Error -> {
                    if (result.message?.contains("Invalid credentials", ignoreCase = true) == true) {
                        _loginState.value = PasswordLoginState.NeedsRegistration(email, password)
                    } else {
                        _loginState.value = PasswordLoginState.Error(result.message ?: "An unknown error occurred")
                    }
                }
                is Result.Loading -> {
                    // Already handled by setting the state before the call, do nothing.
                }
                else -> {
                    _loginState.value = PasswordLoginState.Error("An unexpected error occurred")
                }
            }
        }
    }
}
