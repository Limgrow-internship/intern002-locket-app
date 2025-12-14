package com.intern002.locketapp.ui.screen.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.AppIcon
import com.intern002.locketapp.databinding.FragmentChangeAppIconBinding
import com.intern002.locketapp.ui.adapter.AppIconAdapter

class ChangeAppIconFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChangeAppIconBinding? = null
    private val binding get() = _binding!!

    private lateinit var appIconAdapter: AppIconAdapter
    private val appIcons = mutableListOf<AppIcon>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeAppIconBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMockData()
        setupRecyclerView()

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.btnApply.setOnClickListener {
            // TODO: Add logic to apply the selected icon
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        appIconAdapter = AppIconAdapter(appIcons) { selectedIcon ->
            appIcons.forEach { it.isSelected = (it.id == selectedIcon.id) }
            appIconAdapter.notifyDataSetChanged()
        }
        binding.rvAppIcons.apply {
            adapter = appIconAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun setupMockData() {
        appIcons.clear()
        appIcons.add(AppIcon("default", R.drawable.ic_app_icon_white, true))
        appIcons.add(AppIcon("green", R.drawable.ic_app_icon_green, false))
        appIcons.add(AppIcon("gold", R.drawable.ic_app_icon_gold, false))
        appIcons.add(AppIcon("pink_gradient", R.drawable.ic_app_icon_pink_gradient, false))
        appIcons.add(AppIcon("blue_gradient", R.drawable.ic_app_icon_blue_gradient, false))
        appIcons.add(AppIcon("light_green_gradient", R.drawable.ic_app_icon_light_green_gradient, false))
        appIcons.add(AppIcon("purple_pink_gradient", R.drawable.ic_app_icon_purple_pink_gradient, false))
        appIcons.add(AppIcon("dark_blue_gradient", R.drawable.ic_app_icon_light_white_gradient, false))
        appIcons.add(AppIcon("dark_purple_gradient", R.drawable.ic_app_icon_dark_purple_gradient, false))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ChangeAppIconFragment"
    }
}
