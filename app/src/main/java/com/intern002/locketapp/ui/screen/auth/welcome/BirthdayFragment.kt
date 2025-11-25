package com.intern002.locketapp.ui.screen.auth.welcome

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
import androidx.navigation.fragment.navArgs
import com.intern002.locketapp.databinding.FragmentBirthdayBinding
import com.intern002.locketapp.ui.viewmodel.RegisterState
import com.intern002.locketapp.ui.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class BirthdayFragment : Fragment() {
    private var _binding: FragmentBirthdayBinding? = null
    private val binding get() = _binding!!

    private val args: BirthdayFragmentArgs by navArgs()
    private val registerViewModel: RegisterViewModel by viewModels()

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
            2 -> 29
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

        updateInfoMessage()
        observeViewModel()
        setupResultListeners()
        setupClickListeners()
    }

    private fun setupResultListeners() {
        childFragmentManager.setFragmentResultListener(NumberPickerDialogFragment.TAG_MONTH, viewLifecycleOwner) { _, bundle ->
            val result = bundle.getInt(NumberPickerDialogFragment.SELECTED_VALUE)
            onMonthSelected(result)
        }

        childFragmentManager.setFragmentResultListener(NumberPickerDialogFragment.TAG_DAY, viewLifecycleOwner) { _, bundle ->
            val result = bundle.getInt(NumberPickerDialogFragment.SELECTED_VALUE)
            onDaySelected(result)
        }
    }

    private fun setupClickListeners() {
        binding.buttonMonth.setOnClickListener {
            val currentMonth = selectedMonth ?: 1
            val dialog = NumberPickerDialogFragment.newInstance(
                NumberPickerDialogFragment.TAG_MONTH,
                "Select Month", 1, 12, currentMonth
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
                "Select Day", 1, maxDays, currentDay
            )
            dialog.show(childFragmentManager, NumberPickerDialogFragment.TAG_DAY)
        }

        binding.buttonContinue.setOnClickListener {
            if (selectedMonth != null && selectedDay != null) {
                val year = Calendar.getInstance().get(Calendar.YEAR) - 20
                val birthdayString = String.format("%d-%02d-%02d", year, selectedMonth, selectedDay)

                if (args.idToken != null) {
                    registerViewModel.onCompleteGoogleRegistrationClicked(
                        args.idToken!!,
                        args.username,
                        birthdayString
                    )
                } else {
                    registerViewModel.onRegisterClicked(
                        args.email,
                        args.username,
                        args.password!!,
                        birthdayString
                    )
                }
            }
        }
    }

    private fun onMonthSelected(value: Int) {
        if (selectedMonth != value) {
            selectedDay = null
            binding.buttonDay.text = "Day"
        }
        selectedMonth = value
        binding.buttonMonth.text = value.toString()
        updateInfoMessage()
    }

    private fun onDaySelected(value: Int) {
        selectedDay = value
        binding.buttonDay.text = value.toString()
        updateInfoMessage()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.registerState.collect { state ->
                    binding.loadingView.isVisible = state is RegisterState.Loading
                    binding.buttonContinue.isEnabled = state !is RegisterState.Loading

                    when (state) {
                        is RegisterState.Success -> {
                            val action = BirthdayFragmentDirections.actionBirthdayFragmentToWelcomeFragment(args.username)
                            findNavController().navigate(action)
                        }
                        is RegisterState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            }
        }
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
