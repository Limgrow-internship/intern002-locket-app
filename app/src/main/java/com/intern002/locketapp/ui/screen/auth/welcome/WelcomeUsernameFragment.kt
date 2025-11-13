package com.intern002.locketapp.ui.screen.auth.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.intern002.locketapp.databinding.FragmentWelcomeUsernameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeUsernameFragment : Fragment() {
    private var _binding: FragmentWelcomeUsernameBinding? = null
    private val binding get() = _binding!!

    // Giả định username được truyền vào qua arguments hoặc lấy từ ViewModel
    private var username: String = "thienvi123"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cập nhật username (nếu có thể lấy từ ViewModel hoặc arguments)
        // Ví dụ:
        // arguments?.getString("username")?.let {
        //     username = it
        // }
        binding.textUsername.text = username

        binding.buttonShareUsername.setOnClickListener {
            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey, check out my new username on Locket: $username")
            startActivity(android.content.Intent.createChooser(shareIntent, "Share username via"))
            Toast.makeText(requireContext(), "Share username clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.buttonContinue.setOnClickListener {

            Toast.makeText(requireContext(), "Continue clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}