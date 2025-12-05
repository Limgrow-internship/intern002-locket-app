package com.intern002.locketapp.ui.viewmodel.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.data.repository.FriendshipRepository
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ShareTarget {
    GENERIC,
    MESSENGER,
    INSTAGRAM,
    TWITTER
}

data class ShareEvent(val shareText: String, val target: ShareTarget)

sealed class FriendListState {
    object Loading : FriendListState()
    data class Success(val users: List<Friend>) : FriendListState()
    data class Error(val message: String) : FriendListState()
}

sealed class SuggestionsState {
    object Loading : SuggestionsState()
    data class Success(val users: List<Friend>) : SuggestionsState()
    data class Error(val message: String) : SuggestionsState()
}

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val user: Friend) : SearchState()
    data class Error(val message: String) : SearchState()
}

@HiltViewModel
class FriendshipViewModel @Inject constructor(
    private val repository: FriendshipRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _friendsListState = MutableStateFlow<FriendListState>(FriendListState.Loading)
    val friendsListState = _friendsListState.asStateFlow()

    private val _suggestionsState = MutableStateFlow<SuggestionsState>(SuggestionsState.Loading)
    val suggestionsState = _suggestionsState.asStateFlow()

    private val _searchResultState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchResultState = _searchResultState.asStateFlow()

    private val _shareEvent = Channel<ShareEvent>()
    val shareEvent = _shareEvent.receiveAsFlow()

    private var searchJob: Job? = null

    fun initializeForFriendsScreen() {
        repository.clearCache()
        getFriendsData()
    }

    fun initializeForSuggestionsScreen() {
        repository.clearCache()
        getFriendsData()
        getSuggestionsData()
    }

    private fun getFriendsData() {
        viewModelScope.launch {
            if (_friendsListState.value !is FriendListState.Success) {
                _friendsListState.value = FriendListState.Loading
            }
            try {
                coroutineScope {
                    val friendsDeferred = async { repository.getFriends() }
                    val pendingDeferred = async { repository.getPendingRequests() }
                    val sentDeferred = async { repository.getSentRequests() }

                    val friends = friendsDeferred.await()
                    val pending = pendingDeferred.await()
                    val sent = sentDeferred.await()

                    _friendsListState.value = FriendListState.Success(pending + sent + friends)
                }
            } catch (e: Exception) {
                _friendsListState.value = FriendListState.Error(e.message ?: "Failed to load data")
            }
        }
    }

    private fun getSuggestionsData() {
        viewModelScope.launch {
            if (_suggestionsState.value !is SuggestionsState.Success) {
                _suggestionsState.value = SuggestionsState.Loading
            }
            try {
                coroutineScope {
                    val suggestionsDeferred = async { repository.getSuggestions() }

                    val suggestions = suggestionsDeferred.await()

                    _suggestionsState.value = SuggestionsState.Success(suggestions)
                }
            } catch (e: Exception) {
                _suggestionsState.value = SuggestionsState.Error(e.message ?: "Failed to load suggestions")
            }
        }
    }

    private fun refreshStatesAfterMutation() {
        viewModelScope.launch {
            val currentSearchState = _searchResultState.value
            if (currentSearchState is SearchState.Success) {
                try {
                    val updatedFriend = repository.searchUser(currentSearchState.user.username, currentSearchState.user.discriminator)
                    _searchResultState.value = SearchState.Success(updatedFriend)
                } catch (e: Exception) {
                    _searchResultState.value = SearchState.Idle
                }
            }
            getFriendsData()
            getSuggestionsData()
        }
    }

    fun onShareProfileClicked(target: ShareTarget = ShareTarget.GENERIC) {
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUserProfile()
                val shareText = "Add me on Locket! My username is ${user.username}#${user.discriminator}"
                _shareEvent.send(ShareEvent(shareText, target))
            } catch (e: Exception) {
            }
        }
    }

    private fun setUpdatingStateForFriend(friendId: String, isUpdating: Boolean) {
        _friendsListState.update {
            if (it is FriendListState.Success) {
                it.copy(users = it.users.map {
                    if (it.id == friendId) it.copy(isUpdating = isUpdating) else it
                })
            } else {
                it
            }
        }
        _suggestionsState.update {
            if (it is SuggestionsState.Success) {
                it.copy(users = it.users.map {
                    if (it.id == friendId) it.copy(isUpdating = isUpdating) else it
                })
            } else {
                it
            }
        }
    }

    fun searchUser(query: String) {
        searchJob?.cancel()

        if (query.isBlank() || !query.contains('#')) {
            _searchResultState.value = SearchState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            _searchResultState.value = SearchState.Loading

            val parts = query.split('#').map { it.trim() }
            if (parts.size != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
                _searchResultState.value = SearchState.Idle
                return@launch
            }

            val username = parts[0]
            val discriminator = parts[1].toIntOrNull()

            if (discriminator == null) {
                _searchResultState.value = SearchState.Idle
                return@launch
            }

            try {
                val currentUser = userRepository.getCurrentUserProfile()
                val foundUser = repository.searchUser(username, discriminator)

                if (foundUser.id == currentUser.id) {
                    _searchResultState.value = SearchState.Idle
                } else {
                    _searchResultState.value = SearchState.Success(foundUser)
                }

            } catch (e: Exception) {
                _searchResultState.value = SearchState.Error("User not found")
            }
        }
    }

    fun addFriend(friend: Friend) {
        viewModelScope.launch {
            setUpdatingStateForFriend(friend.id, true)
            try {
                repository.sendFriendRequest(friend.username, friend.discriminator)
                refreshStatesAfterMutation()
            } catch (e: Exception) {
                _searchResultState.value = SearchState.Error("Failed to send request: ${e.message}")
                setUpdatingStateForFriend(friend.id, false)
            }
        }
    }

    fun acceptRequest(friend: Friend) {
        viewModelScope.launch {
            setUpdatingStateForFriend(friend.id, true)
            try {
                repository.acceptFriendRequest(friend.id)
                refreshStatesAfterMutation()
            } catch (e: Exception) {
                setUpdatingStateForFriend(friend.id, false)
            }
        }
    }

    fun deleteFriendship(friend: Friend) {
        viewModelScope.launch {
            setUpdatingStateForFriend(friend.id, true)
            try {
                repository.deleteFriendship(friend.id)
                refreshStatesAfterMutation()
            } catch (e: Exception) {
                setUpdatingStateForFriend(friend.id, false)
            }
        }
    }

    fun rejectRequest(friend: Friend) {
        viewModelScope.launch {
            setUpdatingStateForFriend(friend.id, true)
            try {
                repository.rejectFriendRequest(friend.id)
                refreshStatesAfterMutation()
            } catch (e: Exception) {
                setUpdatingStateForFriend(friend.id, false)
            }
        }
    }
}
