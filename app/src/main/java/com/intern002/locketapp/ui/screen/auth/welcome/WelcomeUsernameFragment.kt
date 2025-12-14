package com.intern002.locketapp.ui.screen.auth.welcome

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentWelcomeUsernameBinding
import com.intern002.locketapp.ui.viewmodel.register.WelcomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeUsernameFragment : Fragment() {
    private var _binding: FragmentWelcomeUsernameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WelcomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchUserProfile()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collect { userProfile ->
                if (userProfile != null) {
                    val fullUsername = "${userProfile.username}#${String.format("%04d", userProfile.discriminator)}"
                    binding.textUsername.text = fullUsername

                    if (userProfile.avatarUrl.isNullOrEmpty()) {
                        binding.imageAvatar.isVisible = false
                        binding.textAvatarInitial.isVisible = true
                        binding.textAvatarInitial.text = userProfile.username.first().uppercase()
                    } else {
                        binding.imageAvatar.isVisible = true
                        binding.textAvatarInitial.isVisible = false
                        Glide.with(requireContext())
                            .load(userProfile.avatarUrl)
                            .placeholder(R.drawable.bg_selected)
                            .into(binding.imageAvatar)
                    }

                    binding.buttonShareUsername.setOnClickListener {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out my new username on Locket: $fullUsername")
                        startActivity(Intent.createChooser(shareIntent, "Share username via"))
                    }
                }
            }
        }

        binding.buttonContinue.setOnClickListener {
            val action = WelcomeUsernameFragmentDirections.actionWelcomeFragmentToAddFriendsFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
