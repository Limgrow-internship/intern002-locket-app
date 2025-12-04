package com.intern002.locketapp.ui.screen.settings.change

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.intern002.locketapp.databinding.FragmentChangeEmailBinding
import com.intern002.locketapp.ui.viewmodel.setting.ChangeEmailViewModel
import com.intern002.locketapp.ui.viewmodel.setting.UpdateEmailState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangeEmailFragment : Fragment() {

    private var _binding: FragmentChangeEmailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChangeEmailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupViews() {
        binding.buttonSave.isEnabled = false
        binding.buttonSave.alpha = 0.5f

        binding.emailEditText.onFocusChangeListener = View.OnFocusChangeListener {
            _, hasFocus ->
            if (hasFocus) {
                binding.emailLayout.suffixText = "@gmail.com"
            } else {
                binding.emailLayout.suffixText = null
            }
        }

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                val isValid = email.isNotBlank()

                binding.buttonSave.isEnabled = isValid
                binding.buttonSave.alpha = if (isValid) 1.0f else 0.5f
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        binding.buttonSave.setOnClickListener {
            val newEmail = binding.emailEditText.text.toString().trim() + "@gmail.com"
            viewModel.updateEmail(newEmail)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.updateState.collectLatest { state ->
                binding.progressBar.isVisible = state is UpdateEmailState.Loading
                binding.buttonSave.isEnabled = state !is UpdateEmailState.Loading

                when (state) {
                    is UpdateEmailState.Success -> {
                        Toast.makeText(requireContext(), "Email updated successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack(R.id.settingsFragment, false)
                    }
                    is UpdateEmailState.Error -> {
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