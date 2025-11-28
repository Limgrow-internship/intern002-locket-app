package com.intern002.locketapp.ui.screen.settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.R
import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.databinding.FragmentSettingsBinding
import com.intern002.locketapp.ui.viewmodel.setting.AvatarUpdateState
import com.intern002.locketapp.ui.viewmodel.setting.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(), EditAvatarBottomSheetFragment.EditAvatarListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var authManager: AuthManager

    private var latestTmpUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri -> viewModel.onAvatarSelected(uri) }
    }

    private val takeImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            latestTmpUri?.let { uri -> viewModel.onAvatarSelected(uri) }
        }
    }

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
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnWidget.setOnClickListener { findNavController().navigate(R.id.action_settingsFragment_to_addWidgetFragment) }
        binding.btnLanguage.setOnClickListener { findNavController().navigate(R.id.action_settingsFragment_to_languageFragment) }
        binding.btnEditExtension.setOnClickListener { findNavController().navigate(R.id.action_settingsFragment_to_extensionsFragment) }
        binding.btnChangeName.setOnClickListener { findNavController().navigate(R.id.action_settingsFragment_to_changeUserNameFragment) }
        binding.btnChangeEmail.setOnClickListener { findNavController().navigate(R.id.action_settingsFragment_to_enterPasswordFragment) }
        binding.btnChangeBirthday.setOnClickListener { findNavController().navigate(R.id.action_settingsFragment_to_changeBirthdayFragment) }
        binding.btnLogout.setOnClickListener { showLogoutConfirmationDialog() }

        binding.btnEditAvatar.setOnClickListener {
            val bottomSheet = EditAvatarBottomSheetFragment()
            bottomSheet.setEditAvatarListener(this)
            bottomSheet.show(childFragmentManager, EditAvatarBottomSheetFragment.TAG)
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.logout_dialog_title))
            .setMessage(getString(R.string.logout_dialog_message))
            .setNegativeButton(getString(R.string.logout_dialog_cancel)) { d, _ -> d.dismiss() }
            .setPositiveButton(getString(R.string.logout_dialog_ok)) { d, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    authManager.clearTokens()
                }
                viewModel.onLogoutClicked()
                d.dismiss()
            }
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.logoutEvent.collect {
                        findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
                    }
                }
                launch {
                    viewModel.avatarUpdateState.collect { state ->
                        binding.loadingView.isVisible = state is AvatarUpdateState.Loading
                        when (state) {
                            is AvatarUpdateState.Success -> {
                                Toast.makeText(requireContext(), "Avatar updated!", Toast.LENGTH_SHORT).show()
                            }
                            is AvatarUpdateState.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    override fun onSelectFromGallery() {
        selectImageLauncher.launch("image/*")
    }

    override fun onTakePicture() {
        lifecycleScope.launch {
            getTmpFileUri().let {
                latestTmpUri = it
                takeImageLauncher.launch(it)
            }
        }
    }

    override fun onDeletePicture() {
        viewModel.onDeleteAvatar()
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}