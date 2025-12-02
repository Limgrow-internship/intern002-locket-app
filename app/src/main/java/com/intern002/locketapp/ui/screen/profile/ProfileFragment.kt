package com.intern002.locketapp.ui.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.CalendarDay
import com.intern002.locketapp.databinding.FragmentProfileBinding
import com.intern002.locketapp.ui.adapter.CalendarAdapter
import com.intern002.locketapp.ui.viewmodel.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupClickListeners()
        setupCalendars()
    }

    override fun onResume() {
        super.onResume()
        // Luôn gọi để lấy profile mới nhất mỗi khi fragment quay trở lại màn hình
        viewModel.fetchUserProfile()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collect { userProfile ->
                if (userProfile != null) {
                    binding.tvName.text = userProfile.username
                    val fullUsername = "${userProfile.username}#${String.format("%04d", userProfile.discriminator)} 🔗"
                    binding.tvUsername.text = fullUsername

                    if (userProfile.avatarUrl.isNullOrEmpty()) {
                        binding.imgAvatar.isVisible = false
                        binding.textAvatarInitial.isVisible = true
                        binding.textAvatarInitial.text = userProfile.username.first().uppercase()
                    } else {
                        binding.imgAvatar.isVisible = true
                        binding.textAvatarInitial.isVisible = false
                        Glide.with(requireContext())
                            .load(userProfile.avatarUrl)
                            .placeholder(R.drawable.avt_sample)
                            .error(R.drawable.avt_sample)
                            .into(binding.imgAvatar)
                    }
                }
            }
        }
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
            findNavController().navigate(R.id.action_profileFragment_to_suggestionFriendsFragment)
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
