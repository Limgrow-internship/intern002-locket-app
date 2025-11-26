package com.intern002.locketapp.ui.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UpdateEmailState {
    object Idle : UpdateEmailState()
    object Loading : UpdateEmailState()
    object Success : UpdateEmailState()
    data class Error(val message: String) : UpdateEmailState()
}

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _updateState = MutableStateFlow<UpdateEmailState>(UpdateEmailState.Idle)
    val updateState = _updateState.asStateFlow()

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            _updateState.value = UpdateEmailState.Loading
            try {
                userRepository.updateEmail(newEmail)
                _updateState.value = UpdateEmailState.Success
            } catch (e: Exception) {
                _updateState.value = UpdateEmailState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}