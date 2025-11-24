package com.intern002.locketapp.ui.screen.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.BottomSheetCaptionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CaptionBottomSheetFragment(
    private val onCaptionSelected: (String) -> Unit
): BottomSheetDialogFragment() {
    private var _binding: BottomSheetCaptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCaptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        binding.btnOptionTime.text = currentTime

        binding.btnOptionTime.setOnClickListener {
            onCaptionSelected(currentTime)
            dismiss()
        }

        val mockWeather = "28°C Sunny ☀️"
        binding.btnOptionWeather.text = "28°C Sunny ☀"
        binding.btnOptionWeather.setOnClickListener {
            onCaptionSelected(mockWeather)
            dismiss()
        }

        val mockLocation = "Da Nang, Vietnam 📍"
        binding.btnOptionLocation.setOnClickListener {
            onCaptionSelected(mockLocation)
            dismiss()
        }

        binding.btnOptionText.setOnClickListener {
            onCaptionSelected("")
            dismiss()
        }

        binding.buttonClose.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int = R.style.Theme_KeepinWidget_BottomSheetDialog

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
