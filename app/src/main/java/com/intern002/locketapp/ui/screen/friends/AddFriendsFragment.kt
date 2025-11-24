package com.intern002.locketapp.ui.screen.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.FriendStatus
import com.intern002.locketapp.data.model.Suggestion
import com.intern002.locketapp.databinding.FragmentAddFriendsBinding
import com.intern002.locketapp.ui.adapter.SuggestionAdapter

class AddFriendsFragment : Fragment() {

    private var _binding: FragmentAddFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var suggestionAdapter: SuggestionAdapter
    private val suggestions = mutableListOf<Suggestion>()
    private var invitedCount = 0
    private val maxFriends = 20

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        updateFriendCount()

        binding.btnContinue.setOnClickListener {
            val action = AddFriendsFragmentDirections.actionAddFriendsFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        suggestions.addAll(createMockSuggestions())
        suggestionAdapter = SuggestionAdapter(suggestions) { suggestion ->
            // Handle Add button click
            suggestion.status = FriendStatus.INVITED
            invitedCount++
            updateFriendCount()
            suggestionAdapter.notifyDataSetChanged()
        }
        binding.rvFriends.apply {
            adapter = suggestionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateFriendCount() {
        binding.tvFriendCount.text = getString(R.string.friends_added_count, invitedCount, maxFriends)
    }

    private fun createMockSuggestions(): List<Suggestion> {
        // Calculate initial invited count
        val mockList = listOf(
            Suggestion("1", "Anh Phạm", "anhpham", null, FriendStatus.INVITED),
            Suggestion("2", "Linh Trần", "linhtran", null, FriendStatus.NOT_FRIEND),
            Suggestion("3", "Tuấn Anh", "tuananh", null, FriendStatus.NOT_FRIEND),
            Suggestion("4", "Minh Nguyễn", "minhnguyen", null, FriendStatus.NOT_FRIEND),
            Suggestion("5", "shreyaaa", "shreya3226", null, FriendStatus.NOT_FRIEND)
        )
        invitedCount = mockList.count { it.status == FriendStatus.INVITED }
        return mockList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
