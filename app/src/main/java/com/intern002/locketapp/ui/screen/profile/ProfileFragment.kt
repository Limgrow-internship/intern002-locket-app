package com.intern002.locketapp.ui.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.CalendarDay
import com.intern002.locketapp.databinding.FragmentProfileBinding
import com.intern002.locketapp.ui.adapter.CalendarAdapter

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupCalendars()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnPremium.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_premiumFragment)
        }
        binding.icSetting.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        binding.icFriends.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_FriendsFragment)
        }
    }

    private fun setupCalendars() {
        binding.rvOctober.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvOctober.adapter = CalendarAdapter(createMockDataForOctober())

        binding.rvNovember.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvNovember.adapter = CalendarAdapter(createMockDataForNovember())
    }

    private fun createMockDataForOctober(): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        // October 2025 starts on a Wednesday (3 placeholders)
        for (i in 0 until 3) { days.add(CalendarDay(0, isPlaceholder = true)) }

        (1..31).forEach { day ->
            val imageRes = when (day) {
                12 -> R.drawable.img_food_sample
                14 -> R.drawable.img_food_sample
                17 -> R.drawable.img_food_sample
                23 -> R.drawable.img_food_sample
                24 -> R.drawable.img_food_sample
                30 -> R.drawable.img_food_sample
                else -> null
            }
            days.add(CalendarDay(day, imageRes = imageRes, showPlusIcon = (day == 27)))
        }
        return days
    }

    private fun createMockDataForNovember(): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        for (i in 0 until 5) { days.add(CalendarDay(0, isPlaceholder = true)) }

        (1..30).forEach { day ->
            val imageRes = if (day == 17) R.drawable.img_food_sample else null
            days.add(CalendarDay(day, imageRes = imageRes))
        }
        return days
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
