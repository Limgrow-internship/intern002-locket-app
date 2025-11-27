package com.intern002.locketapp.ui.screen.friends

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentFriendsBinding
import com.intern002.locketapp.ui.adapter.FriendsAdapter
import com.intern002.locketapp.ui.viewmodel.friends.FriendsUiState
import com.intern002.locketapp.ui.viewmodel.friends.FriendsViewModel
import com.intern002.locketapp.ui.viewmodel.friends.FriendshipStatus
import com.intern002.locketapp.ui.viewmodel.friends.SearchUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FriendsViewModel by viewModels()
    private lateinit var friendsAdapter: FriendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        setupSearch()
    }

    private fun setupRecyclerView() {
        friendsAdapter = FriendsAdapter()
        binding.rvFriends.apply {
            adapter = friendsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        // TODO: Add click listener for the search result card to send a friend request
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchUser(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe friends list
                launch {
                    viewModel.friendsUiState.collect { state ->
                        binding.rvFriends.isVisible = state is FriendsUiState.Success
                        if (state is FriendsUiState.Success) {
                            val friendCount = state.friends.size
                            binding.tvYourFriends.text = getString(R.string.your_friends_count, friendCount, 20)
                            friendsAdapter.submitList(state.friends)
                        }
                    }
                }

                // Observe search result
                launch {
                    viewModel.searchUiState.collect { state ->
                        binding.cvSearchResult.isVisible = state !is SearchUiState.Idle
                        binding.pbSearchLoading.isVisible = state is SearchUiState.Loading
                        binding.ivSearchStatus.isVisible = state is SearchUiState.Success

                        when (state) {
                            is SearchUiState.Success -> {
                                val user = state.user
                                binding.tvSearchUsername.text = user.username
                                binding.tvSearchDiscriminator.text = "#${user.discriminator}"
                                Glide.with(requireContext()).load(user.avatarUrl).placeholder(R.drawable.avt_sample).into(binding.ivSearchAvatar)

                                val statusIcon = when (state.status) {
                                    FriendshipStatus.FRIEND -> R.drawable.ic_friend
                                    FriendshipStatus.NOT_FRIEND -> R.drawable.ic_add_friend
                                    FriendshipStatus.PENDING -> R.drawable.ic_invited // You need to add this drawable
                                    FriendshipStatus.SELF -> 0 // Hide the icon if it's the user themselves
                                }
                                if (statusIcon != 0) {
                                    binding.ivSearchStatus.setImageResource(statusIcon)
                                    binding.ivSearchStatus.visibility = View.VISIBLE
                                } else {
                                    binding.ivSearchStatus.visibility = View.INVISIBLE
                                }
                            }
                            is SearchUiState.NotFound -> {
                                binding.tvSearchUsername.text = "User not found"
                                binding.tvSearchDiscriminator.text = ""
                                binding.ivSearchAvatar.setImageResource(R.drawable.avt_sample)
                                binding.ivSearchStatus.visibility = View.INVISIBLE
                            }
                            is SearchUiState.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                                viewModel.clearSearch() // Reset on error
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
