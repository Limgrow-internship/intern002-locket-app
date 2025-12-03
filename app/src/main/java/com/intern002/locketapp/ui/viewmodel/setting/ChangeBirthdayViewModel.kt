package com.intern002.locketapp.ui.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UpdateBirthdayState {
    object Idle : UpdateBirthdayState()
    object Loading : UpdateBirthdayState()
    object Success : UpdateBirthdayState()
    data class Error(val message: String) : UpdateBirthdayState()
}

@HiltViewModel
class ChangeBirthdayViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _updateState = MutableStateFlow<UpdateBirthdayState>(UpdateBirthdayState.Idle)
    val updateState = _updateState.asStateFlow()

    fun updateBirthday(birthday: String) {
        viewModelScope.launch {
            _updateState.value = UpdateBirthdayState.Loading
            try {
                userRepository.updateBirthday(birthday)
                _updateState.value = UpdateBirthdayState.Success
            } catch (e: Exception) {
                _updateState.value = UpdateBirthdayState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
