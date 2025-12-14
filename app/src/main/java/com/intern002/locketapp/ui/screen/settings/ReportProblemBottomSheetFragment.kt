package com.intern002.locketapp.ui.screen.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intern002.locketapp.databinding.BottomSheetReportProblemBinding

class ReportProblemBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetReportProblemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetReportProblemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSubmitReport.setOnClickListener {
            val email = binding.etReportEmail.text.toString().trim()
            val message = binding.etReportMessage.text.toString().trim()

            if (email.isEmpty() || message.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- Handle the report submission here --- //
            // For now, let's just show a Toast
            val reportMessage = "Email: $email\nMessage: $message"
            Toast.makeText(requireContext(), "Report submitted!\n$reportMessage", Toast.LENGTH_LONG).show()

            dismiss() // Close the bottom sheet
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ReportProblemBottomSheetFragment"
    }
}
