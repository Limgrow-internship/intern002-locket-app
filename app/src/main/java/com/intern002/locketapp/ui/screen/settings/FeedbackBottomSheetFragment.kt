package com.intern002.locketapp.ui.screen.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intern002.locketapp.databinding.BottomSheetFeedbackBinding

class FeedbackBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFeedbackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSubmitFeedback.setOnClickListener {
            val rating = binding.ratingBar.rating
            val message = binding.etFeedbackMessage.text.toString().trim()

            if (rating == 0f) {
                Toast.makeText(requireContext(), "Please provide a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val feedbackMessage = "Rated: $rating stars\nMessage: $message"
            Toast.makeText(requireContext(), feedbackMessage, Toast.LENGTH_LONG).show()

            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FeedbackBottomSheetFragment"
    }
}
