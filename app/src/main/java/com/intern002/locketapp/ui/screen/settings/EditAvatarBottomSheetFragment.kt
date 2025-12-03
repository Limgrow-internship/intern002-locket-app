package com.intern002.locketapp.ui.screen.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intern002.locketapp.databinding.BottomSheetEditAvatarBinding

class EditAvatarBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEditAvatarBinding? = null
    private val binding get() = _binding!!

    interface EditAvatarListener {
        fun onSelectFromGallery()
        fun onTakePicture()
        fun onDeletePicture()
    }

    private var listener: EditAvatarListener? = null

    fun setEditAvatarListener(listener: EditAvatarListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetEditAvatarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSelectGallery.setOnClickListener {
            listener?.onSelectFromGallery()
            dismiss()
        }

        binding.tvTakePicture.setOnClickListener {
            listener?.onTakePicture()
            dismiss()
        }

        binding.tvDeletePicture.setOnClickListener {
            listener?.onDeletePicture()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EditAvatarBottomSheetFragment"
    }
}