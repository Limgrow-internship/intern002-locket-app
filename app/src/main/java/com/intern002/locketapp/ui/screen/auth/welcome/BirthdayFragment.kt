package com.intern002.locketapp.ui.screen.auth.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentBirthdayBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BirthdayFragment : Fragment(), NumberPickerDialogFragment.NumberPickerListener {
    private var _binding: FragmentBirthdayBinding? = null
    private val binding get() = _binding!!

    private var selectedMonth: Int? = null
    private var selectedDay: Int? = null

    private val MONTHS = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    private fun getDaysInMonth(month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> {
                29
            }
            else -> 31
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBirthdayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvInfoText.visibility = View.GONE
        binding.tvWarningMessage.visibility = View.GONE

        selectedMonth?.let { binding.buttonMonth.text = it.toString() }
        selectedDay?.let { binding.buttonDay.text = it.toString() }

        updateInfoMessage()

        binding.buttonMonth.setOnClickListener {
            val currentMonth = selectedMonth ?: 1
            val dialog = NumberPickerDialogFragment.newInstance(
                NumberPickerDialogFragment.TAG_MONTH,
                "Select Month",
                1, 12,
                currentMonth
            )
            dialog.show(childFragmentManager, NumberPickerDialogFragment.TAG_MONTH)
        }

        binding.buttonDay.setOnClickListener {
            if (selectedMonth == null) {
                binding.tvWarningMessage.visibility = View.VISIBLE
                binding.tvInfoText.visibility = View.GONE
                return@setOnClickListener
            }

            binding.tvWarningMessage.visibility = View.GONE

            val maxDays = getDaysInMonth(selectedMonth!!)
            val currentDay = selectedDay?.let { if (it > maxDays) 1 else it } ?: 1

            val dialog = NumberPickerDialogFragment.newInstance(
                NumberPickerDialogFragment.TAG_DAY,
                "Select Day",
                1, maxDays,
                currentDay
            )
            dialog.show(childFragmentManager, NumberPickerDialogFragment.TAG_DAY)
        }

        binding.buttonContinue.setOnClickListener {
            if (selectedMonth != null && selectedDay != null) {
                val birthday = "$selectedDay/$selectedMonth"
                findNavController().navigate(R.id.action_birthdayFragment_to_welcomeFragment)
            }
        }
    }

    override fun onNumberSelected(tag: String, value: Int) {
        when (tag) {
            NumberPickerDialogFragment.TAG_MONTH -> {
                if (selectedMonth != value) {
                    selectedDay = null
                    binding.buttonDay.text = "Day"
                }
                selectedMonth = value
                binding.buttonMonth.text = value.toString()
            }
            NumberPickerDialogFragment.TAG_DAY -> {
                selectedDay = value
                binding.buttonDay.text = value.toString()
            }
        }

        updateInfoMessage()
    }

    private fun updateInfoMessage() {
        if (selectedMonth != null && selectedDay != null) {
            val monthName = MONTHS[selectedMonth!! - 1]
            binding.tvInfoText.text = "You have selected $monthName $selectedDay."

            binding.tvInfoText.visibility = View.VISIBLE
            binding.tvWarningMessage.visibility = View.GONE

            binding.buttonContinue.isEnabled = true
            binding.buttonContinue.alpha = 1.0f

        } else {
            binding.tvInfoText.visibility = View.GONE

            binding.buttonContinue.isEnabled = false
            binding.buttonContinue.alpha = 0.5f

            if (selectedMonth != null) {
                binding.tvWarningMessage.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}