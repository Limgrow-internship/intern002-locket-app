package com.intern002.locketapp.ui.screen.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.Message
import com.intern002.locketapp.databinding.FragmentChatDetailBinding
import com.intern002.locketapp.ui.adapter.MessageAdapter

class ChatDetailFragment : Fragment() {

    private var _binding: FragmentChatDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ChatDetailFragmentArgs by navArgs()
    private lateinit var messageAdapter: MessageAdapter

    private val currentUserId = "my_id"

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

        binding.toolbarTitle.text = args.recipientName

        setupRecyclerView()

        binding.btnBack.setOnClickListener {
            val action = ChatDetailFragmentDirections.actionChatDetailFragmentToChatListFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        val mockMessages = createMockMessages()
        messageAdapter = MessageAdapter(mockMessages, currentUserId)
        binding.rvMessages.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
        }
    }

    private fun createMockMessages(): List<Message> {
        return listOf(
            Message("1", "", "user_id_friend", 1, imageUrl = "img_food_sample"),
            Message("2", "Đi mà không rủ", "user_id_friend", 2),
            Message("3", "Buồn bạn quá đi", "user_id_friend", 3),
            Message("4", "Hôm qua có rủ rồi mà", "my_id", 4),
            Message("5", "Nhắn không trả lời", "my_id", 5),
            Message("6", "Gì z trời", "my_id", 6)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
