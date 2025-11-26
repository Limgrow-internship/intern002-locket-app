package com.intern002.locketapp.ui.screen.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentHomeBinding
import com.intern002.locketapp.ui.screen.main.MainContainerFragmentDirections
import com.intern002.locketapp.ui.viewmodel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var imageCapture: ImageCapture? = null
    private var isFlashOn = false
    private val handler = Handler(Looper.getMainLooper())
    private var isRecording = false

    private val LONG_PRESS_DURATION = 350L
    private var isLongPressTriggered = false
    private val longPressRunnable = Runnable {
        isLongPressTriggered = true
        isRecording = true
        startRecording()

        binding.buttonShutter.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
        binding.buttonShutter.setImageResource(R.drawable.ic_recording_red)
        binding.buttonShutter.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).start()
    }

    private val MAX_VIDEO_DURATION = 3000L
    private val autoStopRunnable = Runnable {
        if (isRecording) {
            Toast.makeText(context, "Complete recording!", Toast.LENGTH_SHORT).show()
            stopRecording()
            binding.buttonShutter.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
            val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false

            if (cameraGranted && audioGranted) {
                startCamera()
            } else if (cameraGranted) {
                Toast.makeText(context, "Need permission for recording audio!", Toast.LENGTH_SHORT)
                    .show()
                startCamera()
            } else {
                Toast.makeText(context, "Need permisson to use app!!", Toast.LENGTH_SHORT).show()
            }
        }

    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("HomeFragment", "Selected URI: $uri")

                val mimeType = requireContext().contentResolver.getType(uri)
                val isVideo = mimeType?.startsWith("video/") == true

                val action = MainContainerFragmentDirections.actionMainContainerFragmentToEditPreviewFragment(
                    mediaUri = uri.toString(),
                    isVideo = isVideo
                )
                findNavController().navigate(action)
            } else {
                Log.d("HomeFragment", "No media selected")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        observeViewModel()
        checkPermissionAndStart()
        setupControls()
    }

    private fun observeViewModel() {
        viewModel.fetchUserProfile()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collect { userProfile ->
                if (userProfile != null) {
                    if (userProfile.avatarUrl.isNullOrEmpty()) {
                        binding.avatar.isVisible = false
                        binding.textAvatarInitial.isVisible = true
                        binding.textAvatarInitial.text = userProfile.username.first().uppercase()
                    } else {
                        binding.avatar.isVisible = true
                        binding.textAvatarInitial.isVisible = false
                        Glide.with(requireContext())
                            .load(userProfile.avatarUrl)
                            .placeholder(R.drawable.avt_sample)
                            .error(R.drawable.avt_sample)
                            .into(binding.avatar)
                    }
                }
            }
        }
    }

    private fun checkPermissionAndStart() {
        val cameraPermission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val audioPermission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)

        if (cameraPermission == PackageManager.PERMISSION_GRANTED && audioPermission == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        }
    }

    fun performCapture() {
        takePhoto()
    }

    fun performFlip() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    fun performOpenGallery() {
        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                .build()

            val preview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .build().also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val recorder = Recorder.Builder()
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.HIGHEST,
                        FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                    )
                )
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            try {
                cameraProvider?.unbindAll()

                camera = cameraProvider?.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    videoCapture
                )

                setupZoomGesture()
            } catch (exc: Exception) {
                Log.e("HomeFragment", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupControls() {
        binding.btnChat.setOnClickListener {
            findNavController().navigate(R.id.action_mainContainerFragment_to_chatListFragment)
        }

        binding.avatarContainer.setOnClickListener {
            findNavController().navigate(R.id.action_mainContainerFragment_to_profileFragment)
        }

        //Feature: Flip Camera
        binding.buttonFlipCamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            isFlashOn = false
            updateFlashUI()
            startCamera()
        }

        //Feature: Flash
        binding.buttonFlash.setOnClickListener {
            if (camera?.cameraInfo?.hasFlashUnit() == true) {
                isFlashOn = !isFlashOn
                camera?.cameraControl?.enableTorch(isFlashOn)
                updateFlashUI()
            } else {
                Toast.makeText(context, "This camera hasn't the flash", Toast.LENGTH_SHORT).show()
            }
        }

        //Feature: Gallery
        binding.buttonGallery.setOnClickListener {
            pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }

        //Feature: Capture and Recording(Touch and hold)
        binding.buttonShutter.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isRecording = false
                    isLongPressTriggered = false
                    // animation for shutter button
                    view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start()
                    handler.postDelayed(longPressRunnable, LONG_PRESS_DURATION)
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(longPressRunnable)
                    view.animate().scaleX(1f).scaleY(1f).setDuration(300).start()
                    binding.buttonShutter.setImageResource(R.drawable.ic_shutter)

                    if (isLongPressTriggered) {
                        if (recording != null) {
                            stopRecording()
                        }
                        isLongPressTriggered = false
                        isRecording = false

                    } else {
                        takePhoto()
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun updateFlashUI() {
        if (isFlashOn) {
            binding.buttonFlash.setImageResource(R.drawable.ic_flash_on)
            binding.buttonFlash.alpha = 1.0f
        } else {
            binding.buttonFlash.setImageResource(R.drawable.ic_flash_off)
            binding.buttonFlash.alpha = 0.7f
        }
    }

    //Feature: Zoom Camera
    private fun setupZoomGesture() {

        //ScaleGestureDetector is Android's class can listen when user touch more than one finger
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                val delta = detector.scaleFactor
                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(requireContext(), listener)
        binding.cameraPreview.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }

        camera?.cameraInfo?.zoomState?.observe(viewLifecycleOwner) { state ->
            val zoomRatio = state.zoomRatio
            val formattedZoom = String.format("%.1fx", zoomRatio)
            binding.textZoom.text = formattedZoom
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())

        // Save photo in external cache directory
        val photoFile = File(requireContext().externalCacheDir, "$name.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        // Animation
        binding.viewFlashOverlay.alpha = 0.8f
        binding.viewFlashOverlay.visibility = View.VISIBLE
        binding.viewFlashOverlay.animate()
            .alpha(0f)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                binding.viewFlashOverlay.visibility = View.GONE
            }
            .start()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("HomeFragment", "Chụp ảnh thất bại: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    val msg = "Saved photo to $savedUri"
                    Log.d("HomeFragment", msg)

                    val action = MainContainerFragmentDirections.actionMainContainerFragmentToEditPreviewFragment(
                        mediaUri = savedUri.toString(),
                        isVideo = false
                    )
                    findNavController().navigate(action)

                    Toast.makeText(requireContext(), "Capture!", Toast.LENGTH_SHORT).show()
                }
            }

        )
    }

    private fun startRecording() {
        val videoCapture = this.videoCapture ?: return

        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())

        val videoFile = File(requireContext().externalCacheDir, "VID_$name.mp4")

        val outputOptions = FileOutputOptions.Builder(videoFile).build()

        val hasAudioPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        recording = videoCapture.output
            .prepareRecording(requireContext(), outputOptions)
            .apply {
                if (hasAudioPermission) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.buttonShutter.setImageResource(R.drawable.ic_recording_red)
                        handler.postDelayed(autoStopRunnable, MAX_VIDEO_DURATION)
                        Log.d("HomeFragment", "Start recording and has audio")
                    }

                    is VideoRecordEvent.Finalize -> {
                        binding.buttonShutter.setImageResource(R.drawable.ic_shutter)
                        handler.removeCallbacks(autoStopRunnable)

                        if (!recordEvent.hasError()) {
                            val savedUri = recordEvent.outputResults.outputUri
                            val msg = "Video saved at: $savedUri"
                            Log.d("HomeFragment", msg)
                            Toast.makeText(requireContext(), "Complete Record", Toast.LENGTH_SHORT)
                                .show()

                            val action =
                                MainContainerFragmentDirections.actionMainContainerFragmentToEditPreviewFragment(
                                    mediaUri = savedUri.toString(),
                                    isVideo = true
                                )
                            findNavController().navigate(action)

                        } else {
                            recording?.close()
                            recording = null
                            Log.e("HomeFragment", "Fail to record: ${recordEvent.error}")
                        }
                    }
                }

            }
    }

    private fun stopRecording() {
        if (recording != null) {
            recording?.stop()
            recording = null
            isRecording = false
            binding.buttonShutter.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}
