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
import androidx.recyclerview.widget.LinearLayoutManager
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentChatListBinding
import com.intern002.locketapp.ui.adapter.ChatListAdapter
import com.intern002.locketapp.ui.viewmodel.chat.ChatListState
import com.intern002.locketapp.ui.viewmodel.chat.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatListAdapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_mainContainerFragment)
        }
    }

    private fun setupRecyclerView() {
        chatListAdapter = ChatListAdapter { conversation ->
            val action = ChatListFragmentDirections.actionChatListFragmentToChatDetailFragment(
                conversationId = conversation.id,
                recipientName = conversation.name,
                recipientAvatarUrl = conversation.avatarUrl
            )
            findNavController().navigate(action)
        }
        binding.rvChatList.apply {
            adapter = chatListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chatListState.collect { state ->
                    binding.pbLoading.isVisible = state is ChatListState.Loading

                    when (state) {
                        is ChatListState.Success -> {
                            val conversations = state.conversations
                            binding.rvChatList.isVisible = conversations.isNotEmpty()
                            binding.tvNoConversations.isVisible = conversations.isEmpty()
                            chatListAdapter.submitList(conversations)
                        }
                        is ChatListState.Error -> {
                            binding.pbLoading.isVisible = false
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        }
                        is ChatListState.Loading -> {
                            binding.rvChatList.isVisible = false
                            binding.tvNoConversations.isVisible = false
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
