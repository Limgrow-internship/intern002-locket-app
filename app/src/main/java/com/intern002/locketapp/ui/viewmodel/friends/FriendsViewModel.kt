package com.intern002.locketapp.ui.viewmodel.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.data.repository.FriendshipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FriendshipStatus { FRIEND, NOT_FRIEND, PENDING, SELF }

sealed class FriendsUiState {
    object Loading : FriendsUiState()
    data class Success(val friends: List<Friend>) : FriendsUiState()
    data class Error(val message: String) : FriendsUiState()
}

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val user: Friend, val status: FriendshipStatus) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
    object NotFound : SearchUiState()
}

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendshipRepository
) : ViewModel() {

    private val _friendsUiState = MutableStateFlow<FriendsUiState>(FriendsUiState.Loading)
    val friendsUiState = _friendsUiState.asStateFlow()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState = _searchUiState.asStateFlow()

    private var searchJob: Job? = null
    private var currentFriends: List<Friend> = emptyList()

    init {
        getFriends()
    }

    fun getFriends() {
        viewModelScope.launch {
            _friendsUiState.value = FriendsUiState.Loading
            try {
                val friends = repository.getFriends()
                currentFriends = friends
                _friendsUiState.value = FriendsUiState.Success(friends)
            } catch (e: Exception) {
                _friendsUiState.value = FriendsUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun searchUser(query: String) {
        searchJob?.cancel()
        if (query.isBlank() || !query.contains("#")) {
            _searchUiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            _searchUiState.value = SearchUiState.Loading

            val parts = query.split("#")
            if (parts.size != 2) {
                _searchUiState.value = SearchUiState.Error("Invalid format. Use username#discriminator")
                return@launch
            }

            val username = parts[0].trim()
            val discriminator = parts[1].trim().toIntOrNull()

            if (discriminator == null) {
                _searchUiState.value = SearchUiState.Error("Invalid discriminator")
                return@launch
            }

            try {
                val foundUser = repository.searchUser(username, discriminator)
                val status = determineFriendshipStatus(foundUser.id)
                _searchUiState.value = SearchUiState.Success(foundUser, status)
            } catch (e: Exception) {
                // Customize based on server response if possible
                _searchUiState.value = SearchUiState.NotFound
            }
        }
    }

    private fun determineFriendshipStatus(userId: String): FriendshipStatus {
        // This logic needs to be improved with actual sent requests list
        // For now, it only checks against the current friends list
        if (currentFriends.any { it.id == userId }) {
            return FriendshipStatus.FRIEND
        } 
        // TODO: Check against current user ID for 'SELF'
        // TODO: Check against sent friend requests list for 'PENDING'
        return FriendshipStatus.NOT_FRIEND
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchUiState.value = SearchUiState.Idle
    }
}
