package com.intern002.locketapp.ui.screen.settings

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.databinding.FragmentBlockedBinding
import com.intern002.locketapp.ui.adapter.BlockedUsersAdapter
import com.intern002.locketapp.ui.viewmodel.friends.BlockedUsersState
import com.intern002.locketapp.ui.viewmodel.friends.FriendshipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BlockedUsersFragment : Fragment() {

    private var _binding: FragmentBlockedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FriendshipViewModel by viewModels()
    private lateinit var blockedUsersAdapter: BlockedUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlockedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBlockedUsers()
    }

    private fun setupRecyclerView() {
        blockedUsersAdapter = BlockedUsersAdapter { user ->
            showUnblockUserDialog(user)
        }
        binding.rvBlockedUsers.apply {
            adapter = blockedUsersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showUnblockUserDialog(user: Friend) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Unblock User")
            .setMessage("Are you sure you want to unblock ${user.username}?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Unblock") { _, _ ->
                viewModel.unblockFriend(user)
            }
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.blockedUsersState.collect { state ->
                    binding.pbLoading.isVisible = state is BlockedUsersState.Loading

                    when (state) {
                        is BlockedUsersState.Success -> {
                            val users = state.users
                            binding.rvBlockedUsers.isVisible = users.isNotEmpty()
                            binding.tvNoBlockedUsers.isVisible = users.isEmpty()
                            blockedUsersAdapter.submitList(users)
                        }
                        is BlockedUsersState.Error -> {
                            binding.pbLoading.isVisible = false
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        }
                        is BlockedUsersState.Loading -> {
                            binding.rvBlockedUsers.isVisible = false
                            binding.tvNoBlockedUsers.isVisible = false
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