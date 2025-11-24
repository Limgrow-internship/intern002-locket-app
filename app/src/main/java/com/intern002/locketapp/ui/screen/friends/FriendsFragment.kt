package com.intern002.locketapp.ui.screen.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.databinding.FragmentFriendsBinding
import com.intern002.locketapp.ui.adapter.FriendAdapter

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var friendAdapter: FriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val mockFriends = createMockFriends()
        friendAdapter = FriendAdapter(mockFriends)
        binding.rvFriends.apply {
            adapter = friendAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.tvYourFriends.text = "Your friends (${mockFriends.size} /20)"
    }

    private fun createMockFriends(): List<Friend> {
        return listOf(
            Friend("1", "Anh Phạm", "anhpham", null),
            Friend("2", "Linh Trần", "linhtran", null),
            Friend("3", "Tuấn Anh", "tuananh", null),
            Friend("4", "Minh Nguyễn", "minhnguyen", null),
            Friend("5", "shreyaaa", "shreya3226", null),
            Friend("6", "Thu Trần", "tranthu", null)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
