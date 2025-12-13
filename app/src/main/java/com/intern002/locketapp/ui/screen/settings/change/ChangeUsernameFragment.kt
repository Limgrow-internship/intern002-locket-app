package com.intern002.locketapp.ui.screen.settings.change

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentChangeUsernameBinding
import com.intern002.locketapp.ui.viewmodel.setting.ChangeUsernameState
import com.intern002.locketapp.ui.viewmodel.setting.ChangeUsernameViewModel
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

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonSave.setOnClickListener {
            val newUsername = binding.usernameEditText.text.toString()
            viewModel.changeUsername(newUsername)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.progressBar.isVisible = state is ChangeUsernameState.Loading
                binding.tvErrorMessage.isVisible = state is ChangeUsernameState.SameUsernameError

                when (state) {
                    is ChangeUsernameState.Success -> {
                        Toast.makeText(context, R.string.username_changed_successfully, Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
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