package com.intern002.locketapp.ui.screen.grid

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.intern002.locketapp.R
import com.intern002.locketapp.data.datasource.MockData
import com.intern002.locketapp.databinding.FragmentGridPostBinding

class GridPostFragment : Fragment(R.layout.fragment_grid_post) {

    private var _binding: FragmentGridPostBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGridPostBinding.bind(view)

        // Setup RecyclerView 3 Cột
        binding.recyclerViewGrid.layoutManager = GridLayoutManager(context, 3)

        val adapter = GridPostAdapter(MockData.posts) { position ->
            // KHI CLICK VÀO ẢNH:
            // 1. Đóng gói vị trí (index)
            setFragmentResult("request_jump_to_post", bundleOf("post_index" to position))

            // 2. Quay về màn hình trước (MainContainer)
            findNavController().popBackStack()
        }

        binding.recyclerViewGrid.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}