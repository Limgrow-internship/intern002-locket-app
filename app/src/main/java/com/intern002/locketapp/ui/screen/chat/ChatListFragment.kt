package com.intern002.locketapp.ui.screen.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentChatListBinding

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

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

        binding.btnBack.setOnClickListener {
            val action = ChatListFragmentDirections.actionChatListFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        val mockData = createMockChatData()
        chatListAdapter = ChatListAdapter(mockData) { conversation ->
            val action = ChatListFragmentDirections.actionChatListFragmentToChatDetailFragment(conversation.id, conversation.name)
            findNavController().navigate(action)
        }
        binding.rvChatList.apply {
            adapter = chatListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createMockChatData(): List<Conversation> {
        return listOf(
            Conversation("1", "Linh Trần", "You: Gì z trời", "12:40 AM", "", true, true),
            Conversation("2", "Anh Phạm", "You: Dễ thương dũ", "11:22 AM", "", true, true),
            Conversation("3", "shrreyaaa", "2 New Messages", "11:15 AM", "", false, false),
            Conversation("4", "Tuấn Anh", "Đi đâu dui dzậy?", "10:55 AM", "", false, false),
            Conversation("5", "Minh Nguyễn", "Minh reacted to your...", "09:21 AM", "", false, true),
            Conversation("6", "Mai Anh", "You: Vẫn một hả", "11:22 AM", "", true, true),
            Conversation("7", "Bích Ngọc", "3 New Messages", "Yesterday", "", false, false),
            Conversation("8", "Khánh Vân", "You: Kh dui", "Yesterday", "", true, false),
            Conversation("9", "Đức Anh", "...", "", "", false, true)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
