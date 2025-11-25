package com.intern002.locketapp.ui.viewmodel.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.AuthRepository
import com.intern002.locketapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EmailValidationState {
    object Idle : EmailValidationState()
    object Loading : EmailValidationState()
    data class Valid(val email: String) : EmailValidationState()
    data class Invalid(val message: String) : EmailValidationState()
    data class Exists(val message: String) : EmailValidationState()
}

@HiltViewModel
class SignupEmailViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _validationState = MutableStateFlow<EmailValidationState>(EmailValidationState.Idle)
    val validationState = _validationState.asStateFlow()

    fun validateEmail(email: String) {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _validationState.value = EmailValidationState.Invalid("Invalid email format.")
            return
        }

        viewModelScope.launch {
            _validationState.value = EmailValidationState.Loading
            when (val result = authRepository.checkEmailExists(email)) {
                is Result.Success -> {
                    if (result.data == true) {
                        _validationState.value = EmailValidationState.Exists("This email is already taken.")
                    } else {
                        _validationState.value = EmailValidationState.Valid(email)
                    }
                }
                is Result.Error -> {
                    _validationState.value = EmailValidationState.Invalid(result.message ?: "Couldn't verify email. Please try again.")
                }
                else -> {}
            }
        }
    }

    fun resetState() {
        _validationState.value = EmailValidationState.Idle
    }
}
