package com.intern002.locketapp.ui.screen.chat

import android.os.Bundle
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
            binding.toolbarAvatarLetter.text = args.recipientName.firstOrNull()?.toString() ?: ""
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}