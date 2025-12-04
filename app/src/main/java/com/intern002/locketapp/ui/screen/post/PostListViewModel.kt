package com.intern002.locketapp.ui.screen.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.data.remote.model.reaction.ReactionTypeResponse
import com.intern002.locketapp.data.repository.PostRepository
import com.intern002.locketapp.data.repository.ReactionRepository
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val reactionRepository: ReactionRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId
    private val _reactionTypes = MutableStateFlow<List<ReactionTypeResponse>>(emptyList())
    val reactionTypes: StateFlow<List<ReactionTypeResponse>> = _reactionTypes

    init {
        fetchCurrentUser()
        loadReactionTypes()
    }

    private var currentPage = 1
    private val pageSize = 20
    private var isLastPage = false
    private var isLoading = false

    init {
        loadPosts(isRefresh = true)
    }

    private fun fetchCurrentUser() {
        viewModelScope.launch {
            try {
                val userProfile = userRepository.getCurrentUserProfile()
                _currentUserId.value = userProfile.id

                loadPosts(isRefresh = true)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadPosts(isRefresh: Boolean = false) {
        if (isLoading) return
        if (isRefresh) {
            currentPage = 1
            isLastPage = false
        } else if (isLastPage) {
            return
        }

        isLoading = true
        viewModelScope.launch {
            val result = postRepository.getPosts(currentPage, pageSize)

            result.onSuccess { newPosts ->
                if (isRefresh) {
                    _posts.value = newPosts
                } else {
                    _posts.value += newPosts
                }

                if (newPosts.size < pageSize) {
                    isLastPage = true
                } else {
                    currentPage++
                }
                isLoading = false
            }

            result.onFailure {
                isLoading = false
            }
        }
    }

    fun refreshFeed() {
        loadPosts(isRefresh = true)
    }

    private fun loadReactionTypes() {
        viewModelScope.launch {
            reactionRepository.getReactionTypes().onSuccess { list ->
                _reactionTypes.value = list
            }
        }
    }

    fun reactToPost(postId: String, reactionTypeId: Int) {
        viewModelScope.launch {
            reactionRepository.reactToPost(postId, reactionTypeId)
                .onSuccess {
                }
                .onFailure {
                }
        }
    }
}