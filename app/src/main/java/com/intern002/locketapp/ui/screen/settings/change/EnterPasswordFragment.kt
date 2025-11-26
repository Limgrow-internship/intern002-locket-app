package com.intern002.locketapp.ui.screen.settings.change

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentEnterPasswordBinding
import com.intern002.locketapp.ui.viewmodel.setting.EnterPasswordViewModel
import com.intern002.locketapp.ui.viewmodel.setting.PasswordVerificationState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EnterPasswordFragment : Fragment() {

    private var _binding: FragmentEnterPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnterPasswordViewModel by viewModels()

    // Khai báo một biến để giữ TextWatcher
    private var passwordTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupViews() {
        binding.buttonContinue.isEnabled = false
        passwordTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonContinue.isEnabled = s.toString().trim().isNotEmpty()
                if (binding.tvErrorMessage.isVisible) {
                    binding.tvErrorMessage.isVisible = false
                    binding.buttonContinue.alpha = 1.0f
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.passwordEditText.addTextChangedListener(passwordTextWatcher)
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonContinue.setOnClickListener {
            val password = binding.passwordEditText.text.toString()
            viewModel.verifyPassword(password)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.verificationState.collectLatest { state ->
                _binding?.let { binding ->
                    binding.progressBar.isVisible = state is PasswordVerificationState.Loading
                    binding.buttonContinue.isEnabled = state !is PasswordVerificationState.Loading

                    when (state) {
                        is PasswordVerificationState.Success -> {
                            findNavController().navigate(R.id.action_enterPasswordFragment_to_changeEmailFragment)
                        }
                        is PasswordVerificationState.IncorrectPasswordError -> {
                            binding.tvErrorMessage.text = getString(R.string.password_incorrect_error)
                            binding.tvErrorMessage.isVisible = true
                            binding.buttonContinue.isEnabled = false
                            binding.buttonContinue.alpha = 0.5f
                        }
                        is PasswordVerificationState.GenericError -> {
                            binding.tvErrorMessage.text = state.message
                            binding.tvErrorMessage.isVisible = true
                            binding.buttonContinue.isEnabled = false
                            binding.buttonContinue.alpha = 0.5f
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.passwordEditText.removeTextChangedListener(passwordTextWatcher)
        passwordTextWatcher = null
        _binding = null
    }
}