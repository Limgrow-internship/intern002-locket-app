package com.intern002.locketapp.ui.screen.settings.change

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentChangeUsernameBinding
import com.intern002.locketapp.ui.viewmodel.setting.ChangeUsernameViewModel
import com.intern002.locketapp.ui.viewmodel.setting.UpdateUsernameState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangeUsernameFragment : Fragment() {

    private var _binding: FragmentChangeUsernameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChangeUsernameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        binding.buttonSave.setOnClickListener {
            val newUsername = binding.usernameEditText.text.toString().trim()
            if (newUsername.isNotEmpty()) {
                viewModel.updateUsername(newUsername)
            } else {
                Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.updateState.collectLatest { state ->
                binding.progressBar.isVisible = state is UpdateUsernameState.Loading
                binding.buttonSave.isEnabled = state !is UpdateUsernameState.Loading

                when (state) {
                    is UpdateUsernameState.Success -> {
                        Toast.makeText(requireContext(), "Username updated successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack(R.id.settingsFragment, false)
                    }
                    is UpdateUsernameState.Error -> {
                        Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}