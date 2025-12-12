package com.intern002.locketapp.ui.screen.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intern002.locketapp.databinding.BottomSheetBlockUserBinding
import com.intern002.locketapp.ui.viewmodel.chat.ChatDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockUserBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBlockUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatDetailViewModel by viewModels({requireParentFragment()})

    companion object {
        const val TAG = "BlockUserBottomSheetFragment"
        const val KEY_USERNAME = "username"

        fun newInstance(username: String): BlockUserBottomSheetFragment {
            return BlockUserBottomSheetFragment().apply {
                arguments = bundleOf(KEY_USERNAME to username)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBlockUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(KEY_USERNAME) ?: "this user"
        binding.tvBlockTitle.text = "Block $username?"

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnBlockConfirm.setOnClickListener {
            viewModel.blockUser()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
