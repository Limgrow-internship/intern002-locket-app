package com.intern002.locketapp.ui.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UpdateUsernameState {
    object Idle : UpdateUsernameState()
    object Loading : UpdateUsernameState()
    object Success : UpdateUsernameState()
    data class Error(val message: String) : UpdateUsernameState()
}

@HiltViewModel
class ChangeUsernameViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _updateState = MutableStateFlow<UpdateUsernameState>(UpdateUsernameState.Idle)
    val updateState = _updateState.asStateFlow()

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            _updateState.value = UpdateUsernameState.Loading
            try {
                userRepository.updateUsername(newUsername)
                _updateState.value = UpdateUsernameState.Success
            } catch (e: Exception) {
                _updateState.value = UpdateUsernameState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}