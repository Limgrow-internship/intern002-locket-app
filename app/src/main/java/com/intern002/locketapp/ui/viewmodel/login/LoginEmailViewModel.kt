package com.intern002.locketapp.ui.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface EmailLoginState {
    object Idle : EmailLoginState
    object Loading : EmailLoginState
    object Success : EmailLoginState
    data class Error(val message: String) : EmailLoginState
}
class LoginEmailViewModel: ViewModel() {
    private var _loginState = MutableStateFlow<EmailLoginState>(EmailLoginState.Idle)
    val loginState: StateFlow<EmailLoginState> = _loginState

    fun onContinueClicked(email: String) {
        _loginState.value = EmailLoginState.Loading

        viewModelScope.launch {
            delay(1000)

            if (email.isBlank()) {
                _loginState.value = EmailLoginState.Error("Email not empty")
            } else if (!email.contains("@gmail.com")) {
                _loginState.value = EmailLoginState.Error("Email don't correct format. That email has must container @gmail.com")
            } else {
                _loginState.value = EmailLoginState.Success
            }
        }
    }

    fun resetState(){
        _loginState.value = EmailLoginState.Idle
    }
}