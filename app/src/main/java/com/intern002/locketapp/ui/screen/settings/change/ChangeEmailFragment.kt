package com.intern002.locketapp.ui.screen.settings.change

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.databinding.FragmentChangeEmailBinding

class ChangeEmailFragment : Fragment() {

    private var _binding: FragmentChangeEmailBinding? = null
    private val binding get() = _binding!!

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

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // TODO: Add logic for the Save button
        // 1. Get current user's email and display it
        // 2. Validate new email
        // 3. Call ViewModel to update email on the server
        // 4. Handle success and error states
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
