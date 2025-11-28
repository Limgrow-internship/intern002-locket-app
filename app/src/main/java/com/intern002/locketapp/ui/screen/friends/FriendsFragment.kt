package com.intern002.locketapp.ui.screen.friends

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.databinding.FragmentFriendsBinding
import com.intern002.locketapp.ui.adapter.FriendsAdapter
import com.intern002.locketapp.ui.viewmodel.friends.FriendshipStatus
import com.intern002.locketapp.ui.viewmodel.friends.FriendsListState
import com.intern002.locketapp.ui.viewmodel.friends.FriendsViewModel
import com.intern002.locketapp.ui.viewmodel.friends.SearchState
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
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        friendsAdapter = FriendsAdapter().apply {
            onItemClickListener = {
                handleFriendItemClick(it)
            }
        }
        binding.rvFriends.apply {
            adapter = friendsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleFriendItemClick(friend: Friend) {
        when (friend.status) {
            FriendshipStatus.PENDING_INCOMING -> showPendingRequestDialog(friend)
            FriendshipStatus.PENDING_OUTGOING -> showSentRequestDialog(friend)
            FriendshipStatus.FRIEND -> showFriendDialog(friend)
            else -> {  }
        }
    }

    private fun showPendingRequestDialog(friend: Friend) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Friend Request")
            .setMessage("Accept friend request from ${friend.username}?")
            .setNegativeButton("Reject") { _, _ ->
                viewModel.rejectRequest(friend)
            }
            .setPositiveButton("Accept") { _, _ ->
                viewModel.acceptRequest(friend)
            }
            .show()
    }

    private fun showSentRequestDialog(friend: Friend) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Request Sent")
            .setMessage("You have already sent a friend request to ${friend.username}.")
            .setNegativeButton("Keep", null)
            .setPositiveButton("Cancel Request Friend") { _, _ ->
                viewModel.rejectRequest(friend)
            }
            .show()
    }

    private fun showFriendDialog(friend: Friend) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Unfriend")
            .setMessage("Are you sure you want to unfriend ${friend.username}?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Unfriend") { _, _ ->
                viewModel.deleteFriendship(friend)
            }
            .show()
    }


    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupSearch() {
        binding.btnSearch.addTextChangedListener(object : TextWatcher {
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
                launch {
                    viewModel.friendsListState.collect { state ->
                        binding.pbFriendsLoading.isVisible = state is FriendsListState.Loading
                        binding.rvFriends.isVisible = state is FriendsListState.Success

                        when (state) {
                            is FriendsListState.Success -> {
                                val friendCount = state.users.filter { it.status == FriendshipStatus.FRIEND }.size
                                binding.tvYourFriends.text = getString(R.string.your_friends_count, friendCount, 20)
                                friendsAdapter.submitList(state.users)
                            }
                            is FriendsListState.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                                binding.tvYourFriends.text = getString(R.string.your_friends)
                            }
                            is FriendsListState.Loading -> {  }
                        }
                    }
                }

                launch {
                    viewModel.searchResultState.collect { state ->
                        binding.cvSearchResult.isVisible = state is SearchState.Success || state is SearchState.Loading
                        binding.pbSearchLoading.isVisible = state is SearchState.Loading
                        binding.rlSearchResult.isVisible = state is SearchState.Success

                        when (state) {
                            is SearchState.Success -> {
                                bindSearchResult(state.user)
                            }
                            is SearchState.Error -> {
                                binding.cvSearchResult.isVisible = false
                                if (state.message.isNotEmpty() && state.message != "User not found") {
                                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                            is SearchState.Loading -> {  }
                            is SearchState.Idle -> {
                                binding.cvSearchResult.isVisible = false
                            }
                        }
                    }
                }
            }
        }
    }

    private fun bindSearchResult(friend: Friend) {
        binding.tvSearchUsername.text = friend.username
        binding.tvSearchDiscriminator.text = "#${friend.discriminator}"

        if (!friend.avatarUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(friend.avatarUrl)
                .placeholder(createInitialDrawable(requireContext(), friend.username))
                .error(createInitialDrawable(requireContext(), friend.username))
                .into(binding.ivSearchAvatar)
        } else {
            binding.ivSearchAvatar.setImageDrawable(createInitialDrawable(requireContext(), friend.username))
        }

        val statusIcon = when (friend.status) {
            FriendshipStatus.FRIEND -> R.drawable.ic_friend
            FriendshipStatus.NOT_FRIEND -> R.drawable.ic_add_friend
            FriendshipStatus.PENDING_INCOMING, FriendshipStatus.PENDING_OUTGOING -> R.drawable.ic_invited
            FriendshipStatus.SELF -> 0 // Or some other indicator for self
        }

        if (statusIcon != 0) {
            binding.ivSearchStatus.setImageResource(statusIcon)
            binding.ivSearchStatus.visibility = View.VISIBLE
        } else {
            binding.ivSearchStatus.visibility = View.INVISIBLE
        }

        binding.ivSearchStatus.setOnClickListener {
            if (friend.status == FriendshipStatus.NOT_FRIEND) {
                viewModel.addFriend(friend)
            }
        }
    }

    private fun createInitialDrawable(context: Context, name: String): BitmapDrawable {
        val size = 150
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.grey_dark)
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, backgroundPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = size / 2f
            textAlign = Paint.Align.CENTER
        }

        val initial = if (name.isNotEmpty()) name.first().uppercase() else ""
        val yPos = (canvas.height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(initial, canvas.width / 2f, yPos, textPaint)

        return BitmapDrawable(context.resources, bitmap)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
