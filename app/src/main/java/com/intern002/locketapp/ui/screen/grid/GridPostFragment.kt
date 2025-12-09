package com.intern002.locketapp.ui.screen.grid

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentGridPostBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GridPostFragment : Fragment(R.layout.fragment_grid_post) {

    private var _binding: FragmentGridPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GridPostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGridPostBinding.bind(view)

        setupRecyclerView()
        observeData()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserProfile()
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(context, 3)
        binding.recyclerViewGrid.layoutManager = layoutManager

        val adapter = GridPostAdapter(emptyList()) { position ->
            setFragmentResult("request_jump_to_post", bundleOf("post_index" to position))
            findNavController().popBackStack()
        }
        binding.recyclerViewGrid.adapter = adapter

        binding.recyclerViewGrid.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (totalItemCount <= lastVisibleItem + 6) {
                }
            }
        })
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfile.collectLatest { userProfile ->
                    if (userProfile != null) {
                        if (userProfile.avatarUrl.isNullOrEmpty()) {
                            binding.avatar.isVisible = false
                            binding.textAvatarInitial.isVisible = true
                            binding.textAvatarInitial.text = userProfile.username.first().uppercase()
                        } else {
                            binding.avatar.isVisible = true
                            binding.textAvatarInitial.isVisible = false
                            Glide.with(requireContext())
                                .load(userProfile.avatarUrl)
                                .placeholder(R.drawable.avt_sample)
                                .error(R.drawable.avt_sample)
                                .into(binding.avatar)
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.avatarContainer.setOnClickListener {
            findNavController().navigate(R.id.action_gridPostFragment_to_profileFragment)
        }

        binding.buttonChat.setOnClickListener {
            findNavController().navigate(R.id.action_gridPostFragment_to_chatListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}