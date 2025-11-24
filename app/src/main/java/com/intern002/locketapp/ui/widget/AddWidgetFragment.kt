package com.intern002.locketapp.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentAddWidgetBinding

class AddWidgetFragment : Fragment() {

    private var _binding: FragmentAddWidgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWidgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val steps = listOf(
            AddWidgetStep(R.drawable.ic_add_widget_step_1, "Press and hold an empty space on your Home Screen"),
            AddWidgetStep(R.drawable.ic_add_widget_step_2, "Tap the Widget button to add a new widget"),
            AddWidgetStep(R.drawable.ic_add_widget_step_3, "Search and add the widget")
        )

        val adapter = AddWidgetAdapter(requireContext(), steps)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // No text to be displayed
        }.attach()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
