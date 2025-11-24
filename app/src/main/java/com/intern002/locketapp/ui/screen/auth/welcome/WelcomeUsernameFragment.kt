package com.intern002.locketapp.ui.screen.auth.welcome

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentWelcomeUsernameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeUsernameFragment : Fragment() {
    private var _binding: FragmentWelcomeUsernameBinding? = null
    private val binding get() = _binding!!

    private val args: WelcomeUsernameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = args.username
        binding.textUsername.text = username

        binding.buttonShareUsername.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out my new username on Locket: $username")
            startActivity(Intent.createChooser(shareIntent, "Share username via"))
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