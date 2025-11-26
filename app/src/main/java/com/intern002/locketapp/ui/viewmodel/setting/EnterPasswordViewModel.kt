package com.intern002.locketapp.ui.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EnterPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _verificationState =
        MutableStateFlow<PasswordVerificationState>(PasswordVerificationState.Idle)
    val verificationState = _verificationState.asStateFlow()

    fun verifyPassword(password: String) {
        viewModelScope.launch {
            _verificationState.value = PasswordVerificationState.Loading
            try {
                val isPasswordCorrect = userRepository.verifyPassword(password)
                if (isPasswordCorrect) {
                    _verificationState.value = PasswordVerificationState.Success
                } else {
                    _verificationState.value = PasswordVerificationState.IncorrectPasswordError
                }
            } catch (e: ClientRequestException) {
                _verificationState.value = PasswordVerificationState.GenericError("Authentication error. Please log in again.")
            } catch (e: IOException) {
                _verificationState.value = PasswordVerificationState.GenericError("Network connection error. Could not connect to the server.")
            } catch (e: Exception) {
                _verificationState.value = PasswordVerificationState.GenericError("An error occurred: ${e.message}")
            }
        }
    }
}

// Trạng thái của việc xác thực mật khẩu
sealed class PasswordVerificationState {
    object Idle : PasswordVerificationState()
    object Loading : PasswordVerificationState()
    object Success : PasswordVerificationState()
    object IncorrectPasswordError : PasswordVerificationState() // Lỗi sai mật khẩu
    data class GenericError(val message: String) : PasswordVerificationState() // Các lỗi khác
}