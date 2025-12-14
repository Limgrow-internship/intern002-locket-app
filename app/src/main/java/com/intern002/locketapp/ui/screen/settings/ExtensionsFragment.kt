package com.intern002.locketapp.ui.screen.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.WidgetFrame
import com.intern002.locketapp.databinding.FragmentExtensionsBinding
import com.intern002.locketapp.ui.adapter.WidgetFrameAdapter

class ExtensionsFragment : Fragment() {

    private var _binding: FragmentExtensionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var frameAdapter: WidgetFrameAdapter
    private val frames = mutableListOf<WidgetFrame>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExtensionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFrames()
        setupRecyclerView()

        binding.ivClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        frameAdapter = WidgetFrameAdapter(frames) { selectedFrame ->
            if (!selectedFrame.isLocked) {
                frames.forEach { it.isSelected = (it.id == selectedFrame.id) }
                frameAdapter.notifyDataSetChanged()
            } else {
                // TODO: Show premium purchase screen or a toast
            }
        }
        binding.rvFrames.apply {
            adapter = frameAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun setupFrames() {
        frames.clear()
        frames.add(WidgetFrame("none", isLocked = false, isSelected = true, backgroundRes = R.drawable.bg_frame_none_selected, displayText = "None"))
        frames.add(WidgetFrame("1", isLocked = true, isSelected = false, backgroundRes = R.drawable.bg_frame_locked_1))
        frames.add(WidgetFrame("2", isLocked = true, isSelected = false, backgroundRes = R.drawable.bg_frame_locked_2))
        frames.add(WidgetFrame("3", isLocked = true, isSelected = false, backgroundRes = R.drawable.bg_frame_locked_3))
        frames.add(WidgetFrame("4", isLocked = true, isSelected = false, backgroundRes = R.drawable.bg_frame_locked_4))
        frames.add(WidgetFrame("5", isLocked = true, isSelected = false, backgroundRes = R.drawable.bg_frame_locked_5))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ExtensionsFragment"
    }
}
