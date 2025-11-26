package com.intern002.locketapp.ui.screen.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intern002.locketapp.R
import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.databinding.FragmentSettingsBinding
import com.intern002.locketapp.ui.viewmodel.setting.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivChangeApp.setOnClickListener {
            ChangeAppIconFragment().show(childFragmentManager, ChangeAppIconFragment.TAG)
        }

        binding.btnWidget.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_addWidgetFragment)
        }

        binding.btnLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_languageFragment)
        }

        binding.btnEditExtension.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_extensionsFragment)
        }

        binding.btnChangeName.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_changeUserNameFragment)
        }

        binding.btnChangeEmail.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_enterPasswordFragment)
        }

        binding.btnChangeBirthday.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_changeBirthdayFragment)
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.logout_dialog_title))
            .setMessage(getString(R.string.logout_dialog_message))
            .setNegativeButton(getString(R.string.logout_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.logout_dialog_ok)) { dialog, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    authManager.clearTokens()
                }
                viewModel.onLogoutClicked()
                dialog.dismiss()
            }
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutEvent.collect {
                    findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}