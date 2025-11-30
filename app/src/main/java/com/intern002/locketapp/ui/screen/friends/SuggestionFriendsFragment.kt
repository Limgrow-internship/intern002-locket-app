package com.intern002.locketapp.ui.screen.friends

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
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
import com.intern002.locketapp.databinding.FragmentSuggestionFriendsBinding
import com.intern002.locketapp.ui.adapter.FriendsAdapter
import com.intern002.locketapp.ui.viewmodel.friends.FriendListState
import com.intern002.locketapp.ui.viewmodel.friends.FriendshipStatus
import com.intern002.locketapp.ui.viewmodel.friends.FriendshipViewModel
import com.intern002.locketapp.ui.viewmodel.friends.SearchState
import com.intern002.locketapp.ui.viewmodel.friends.ShareTarget
import com.intern002.locketapp.ui.viewmodel.friends.SuggestionsState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SuggestionFriendsFragment : Fragment() {

    private var _binding: FragmentSuggestionFriendsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FriendshipViewModel by viewModels()
    private lateinit var suggestionsAdapter: FriendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuggestionFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initializeForSuggestionsScreen()

        setupRecyclerView()
        setupClickListeners()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        suggestionsAdapter = FriendsAdapter().apply {
            onItemClickListener = { friend ->
                handleFriendItemClick(friend)
            }
        }
        binding.rvFriends.apply {
            adapter = suggestionsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleFriendItemClick(friend: Friend) {
        when (friend.status) {
            FriendshipStatus.NOT_FRIEND -> viewModel.addFriend(friend)
            FriendshipStatus.PENDING_OUTGOING -> showSentRequestDialog(friend)
            else -> { }
        }
    }

    private fun showSentRequestDialog(friend: Friend) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Request Sent")
            .setMessage("You have already sent a friend request to ${friend.username}.")
            .setNegativeButton("Cancel Request") { _, _ ->
                viewModel.rejectRequest(friend)
            }
            .setPositiveButton("Keep", null)
            .show()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnMore.setOnClickListener {
            viewModel.onShareProfileClicked(ShareTarget.GENERIC)
        }
        binding.btnMessenger.setOnClickListener {
            viewModel.onShareProfileClicked(ShareTarget.MESSENGER)
        }
        binding.btnInstagram.setOnClickListener {
            viewModel.onShareProfileClicked(ShareTarget.INSTAGRAM)
        }
        binding.btnTwitter.setOnClickListener {
            viewModel.onShareProfileClicked(ShareTarget.TWITTER)
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
                    viewModel.suggestionsState.collect { state ->
                        binding.pbSuggestionsLoading.isVisible = state is SuggestionsState.Loading
                        binding.rvFriends.isVisible = state is SuggestionsState.Success

                        if (state is SuggestionsState.Success) {
                            suggestionsAdapter.submitList(state.users)
                        }
                    }
                }

                launch {
                    viewModel.friendsListState.collect { state ->
                        if (state is FriendListState.Success) {
                            val friendCount = state.users.count { it.status == FriendshipStatus.FRIEND }
                            binding.tvYourFriends.text = getString(R.string.suggestions_count, friendCount, 20)
                        }
                    }
                }

                launch {
                    viewModel.searchResultState.collect { state ->
                        val isSuccess = state is SearchState.Success
                        binding.cvSearchResult.isVisible = isSuccess || state is SearchState.Loading
                        binding.pbSearchLoading.isVisible = state is SearchState.Loading
                        binding.rlSearchResult.isVisible = isSuccess

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
                            is SearchState.Loading -> { }
                            is SearchState.Idle -> {
                                binding.cvSearchResult.isVisible = false
                            }
                        }
                    }
                }

                launch {
                    viewModel.shareEvent.collect { event ->
                        when (event.target) {
                            ShareTarget.GENERIC -> shareGeneric(event.shareText)
                            ShareTarget.MESSENGER -> shareToTargetedApp(event.shareText, "com.facebook.orca", "Messenger")
                            ShareTarget.INSTAGRAM -> shareToTargetedApp(event.shareText, "com.instagram.android", "Instagram")
                            ShareTarget.TWITTER -> shareToTargetedApp(event.shareText, "com.twitter.android", "Twitter")
                        }
                    }
                }
            }
        }
    }

    private fun shareGeneric(shareText: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun shareToTargetedApp(shareText: String, packageName: String, appName: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            setPackage(packageName)
        }
        try {
            startActivity(sendIntent)
        } catch (e: ActivityNotFoundException) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
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
            FriendshipStatus.SELF -> 0
        }

        if (statusIcon != 0) {
            binding.ivSearchStatus.setImageResource(statusIcon)
            binding.ivSearchStatus.visibility = View.VISIBLE
        } else {
            binding.ivSearchStatus.visibility = View.INVISIBLE
        }

        binding.ivSearchStatus.setOnClickListener {
             handleFriendItemClick(friend)
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
