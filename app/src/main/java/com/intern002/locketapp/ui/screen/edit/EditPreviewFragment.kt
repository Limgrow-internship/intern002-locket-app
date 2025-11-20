package com.intern002.locketapp.ui.screen.edit

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.intern002.locketapp.databinding.FragmentEditPreviewBinding
import com.intern002.locketapp.utils.MediaSaver
import kotlinx.coroutines.launch

class EditPreviewFragment: Fragment() {
    private var _binding: FragmentEditPreviewBinding? = null
    private val binding get() = _binding!!

    private val args: EditPreviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaUri = args.mediaUri.toUri()
        val isVideo = args.isVideo

        if (isVideo) {
            setupVideo(mediaUri)
        } else {
            setupImage(mediaUri)
        }
        setupButtons(mediaUri, isVideo)
    }

    private fun setupImage(uri : Uri) {
        binding.imagePreview.visibility = View.VISIBLE
        binding.videoPreview.visibility = View.GONE

        Glide.with(this).load(uri).into(binding.imagePreview)
    }

    private fun setupVideo(uri : Uri) {
        binding.imagePreview.visibility = View.GONE
        binding.videoPreview.visibility = View.VISIBLE
        binding.videoPreview.setVideoURI(uri)

        binding.videoPreview.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }
    }

    private fun setupButtons(uri: Uri, isVideo: Boolean) {

        binding.buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonSend.setOnClickListener {
            val caption = binding.editTextCaption.text.toString()
            Toast.makeText(context, "Sending with caption: $caption", Toast.LENGTH_SHORT).show()
        }

        binding.buttonDownload.setOnClickListener {
            lifecycleScope.launch {
                Toast.makeText(context, "Saving...", Toast.LENGTH_SHORT).show()

                MediaSaver.saveMediaToGallery(requireContext(), uri, isVideo)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}