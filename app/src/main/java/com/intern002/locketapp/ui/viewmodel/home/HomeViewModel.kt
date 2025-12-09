package com.intern002.locketapp.ui.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.model.UserProfile
import com.intern002.locketapp.data.repository.FriendshipRepository
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _friendCount = MutableStateFlow<Int>(0)
    val friendCount: StateFlow<Int> = _friendCount

    init {
        fetchUserProfile()
        fetchFriendCount()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUserProfile()
                _userProfile.value = user
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun fetchFriendCount() {
        viewModelScope.launch {
            try {
                val friends = friendshipRepository.getFriends()
                _friendCount.value = friends.size
            } catch (e: Exception) {
                _friendCount.value = 0
            }
        }
    }
}