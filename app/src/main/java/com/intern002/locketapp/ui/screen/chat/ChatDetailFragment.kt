package com.intern002.locketapp.ui.screen.chat

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TouchDelegate
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intern002.locketapp.R
import com.intern002.locketapp.data.repository.UserRepository
import com.intern002.locketapp.databinding.FragmentChatDetailBinding
import com.intern002.locketapp.ui.adapter.MessageAdapter
import com.intern002.locketapp.ui.viewmodel.chat.ChatDetailViewModel
import com.intern002.locketapp.ui.viewmodel.chat.MessageListState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatDetailFragment : Fragment() {

    private var _binding: FragmentChatDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ChatDetailFragmentArgs by navArgs()
    private val viewModel: ChatDetailViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

    @Inject
    lateinit var userRepository: UserRepository

    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        lifecycleScope.launch {
            currentUserId = userRepository.getCurrentUserProfile()?.id
            if (currentUserId != null) {
                setupRecyclerView(currentUserId!!)
            }
        }

        observeViewModel()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbarTitle.text = args.recipientName
        val avatarUrl = args.recipientAvatarUrl
        if (!avatarUrl.isNullOrEmpty()) {
            binding.toolbarAvatar.isVisible = true
            binding.toolbarAvatarLetter.isVisible = false
            Glide.with(this)
                .load(avatarUrl)
                .into(binding.toolbarAvatar)
        } else {
            binding.toolbarAvatar.isVisible = false
            binding.toolbarAvatarLetter.isVisible = true
            binding.toolbarAvatarLetter.text = args.recipientName.firstOrNull()?.toString()?.uppercase() ?: ""
        }
    }

    private fun setupRecyclerView(userId: String) {
        messageAdapter = MessageAdapter(
            mutableListOf(),
            userId,
            args.recipientAvatarUrl,
            args.recipientName
        )
        binding.rvMessages.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.messageState.collect { state ->
                        binding.progressBar.isVisible = state is MessageListState.Loading
                        binding.rvMessages.isVisible = state is MessageListState.Success

                        when (state) {
                            is MessageListState.Success -> {
                                val newMessages = state.messages
                                if (messageAdapter.itemCount < newMessages.size) {
                                    messageAdapter.setMessages(newMessages)
                                    binding.rvMessages.scrollToPosition(newMessages.size - 1)
                                } else {
                                    messageAdapter.setMessages(newMessages)
                                }
                            }
                            is MessageListState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }

                launch {
                    viewModel.isBlocked.collect { isBlocked ->
                        binding.inputContainer.isVisible = !isBlocked
                        binding.tvBlockedMessage.isVisible = isBlocked
                        // Update menu based on block status
                        binding.chatMenu.optionUnblockFriend.isVisible = isBlocked
                        binding.chatMenu.optionRemoveFriend.isVisible = !isBlocked
                        binding.chatMenu.optionBlockFriend.isVisible = !isBlocked
                    }
                }

                launch {
                    viewModel.removeFriendEvent.collect { 
                        findNavController().navigate(R.id.action_chatDetailFragment_to_chatListFragment)
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSend.setOnClickListener { 
            val messageText = binding.etMessage.text.toString()
            if (messageText.isNotBlank()) {
                viewModel.sendMessage(messageText)
                binding.etMessage.text.clear()
            }
        }

        binding.btnMore.setOnClickListener {
            toggleMenuVisibility(true)
        }

        binding.scrimView.setOnClickListener {
            toggleMenuVisibility(false)
        }

        binding.chatMenu.optionRemoveFriend.setOnClickListener {
            showRemoveFriendDialog()
            toggleMenuVisibility(false)
        }

        binding.chatMenu.optionBlockFriend.setOnClickListener {
            showBlockFriendDialog()
            toggleMenuVisibility(false)
        }

        binding.chatMenu.optionUnblockFriend.setOnClickListener {
            showUnblockFriendDialog()
            toggleMenuVisibility(false)
        }

        val parent = binding.btnMore.parent as View
        parent.post {
            val rect = Rect()
            binding.btnMore.getHitRect(rect)
            val expansion = (24 * resources.displayMetrics.density).toInt() // 24dp
            rect.top -= expansion
            rect.bottom += expansion
            rect.left -= expansion
            rect.right += expansion
            parent.touchDelegate = TouchDelegate(rect, binding.btnMore)
        }
    }

    private fun showRemoveFriendDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Unfriend")
            .setMessage("Are you sure you want to unfriend ${args.recipientName}?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Unfriend") { _, _ ->
                viewModel.removeFriend()
            }
            .show()
    }

    private fun showBlockFriendDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Block User")
            .setMessage("Are you sure you want to block ${args.recipientName}? You will no longer be able to send or receive messages.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Block") { _, _ ->
                viewModel.blockUser()
            }
            .show()
    }

    private fun showUnblockFriendDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Unblock User")
            .setMessage("Are you sure you want to unblock ${args.recipientName}?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Unblock") { _, _ ->
                viewModel.unblockUser()
            }
            .show()
    }

    private fun toggleMenuVisibility(show: Boolean) {
        binding.chatMenu.root.isVisible = show
        binding.scrimView.isVisible = show
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
