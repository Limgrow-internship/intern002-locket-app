package com.intern002.locketapp.ui.screen.grid

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                    viewModel.loadPosts(isRefresh = false)
                }
            }
        })
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.posts.collectLatest { postList ->
                    val adapter = binding.recyclerViewGrid.adapter as? GridPostAdapter
                    adapter?.updateData(postList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}