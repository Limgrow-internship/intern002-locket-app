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
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ShareTarget {
    GENERIC,
    MESSENGER,
    INSTAGRAM,
    TWITTER
}

data class ShareEvent(val shareText: String, val target: ShareTarget)

sealed class FriendsListState {
    object Loading : FriendsListState()
    data class Success(val users: List<Friend>) : FriendsListState()
    data class Error(val message: String) : FriendsListState()
}

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val user: Friend) : SearchState()
    data class Error(val message: String) : SearchState()
}

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendshipRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _friendsListState = MutableStateFlow<FriendsListState>(FriendsListState.Loading)
    val friendsListState = _friendsListState.asStateFlow()

    private val _searchResultState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchResultState = _searchResultState.asStateFlow()

    private val _shareEvent = Channel<ShareEvent>()
    val shareEvent = _shareEvent.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        getFriendsData()
    }

    private fun getFriendsData() {
        viewModelScope.launch {
            _friendsListState.value = FriendsListState.Loading
            try {
                coroutineScope {
                    val friendsDeferred = async { repository.getFriends() }
                    val pendingDeferred = async { repository.getPendingRequests() }
                    val sentDeferred = async { repository.getSentRequests() }

                    val friends = friendsDeferred.await()
                    val pending = pendingDeferred.await()
                    val sent = sentDeferred.await()

                    _friendsListState.value = FriendsListState.Success(pending + sent + friends)
                }
            } catch (e: Exception) {
                _friendsListState.value = FriendsListState.Error(e.message ?: "Failed to load data")
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
        }
    }

    fun onShareProfileClicked(target: ShareTarget = ShareTarget.GENERIC) {
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUserProfile()
                val shareText = "Add me on Locket! My username is ${user.username}#${user.discriminator}"
                _shareEvent.send(ShareEvent(shareText, target))
            } catch (e: Exception) {
                // Handle error if necessary
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
            try {
                repository.sendFriendRequest(friend.username, friend.discriminator)
                val updatedFriend = friend.copy(status = FriendshipStatus.PENDING_OUTGOING)
                _searchResultState.value = SearchState.Success(updatedFriend)
                getFriendsData()
            } catch (e: Exception) {
                _searchResultState.value = SearchState.Error("Failed to send request: ${e.message}")
            }
        }
    }

    fun acceptRequest(friend: Friend) {
        viewModelScope.launch {
            try {
                repository.acceptFriendRequest(friend.id)
                refreshStatesAfterMutation()
            } catch (e: Exception) {
            }
        }
    }

    fun deleteFriendship(friend: Friend) {
        viewModelScope.launch {
            try {
                repository.deleteFriendship(friend.id)
                refreshStatesAfterMutation()
            } catch (e: Exception) {
            }
        }
    }

    fun rejectRequest(friend: Friend) {
        viewModelScope.launch {
            try {
                repository.rejectFriendRequest(friend.id)
                refreshStatesAfterMutation()
            } catch (e: Exception) {
            }
        }
    }
}
