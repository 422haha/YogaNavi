package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import android.Manifest
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.databinding.FragmentLectureVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.core.MainActivity
import com.ssafy.yoganavi.ui.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@AndroidEntryPoint
class LectureVideoFragment : BaseFragment<FragmentLectureVideoBinding>(
    FragmentLectureVideoBinding::inflate
) {
    private val args by navArgs<LectureVideoFragmentArgs>()
    private val viewModel: LectureVideoViewModel by viewModels()
    private lateinit var player: ExoPlayer
    private val width by lazy { binding.poseView.width }
    private val height by lazy { binding.poseView.height }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PermissionHelper(this, arrayOf(Manifest.permission.CAMERA), ::popBack)
            .launchPermission()

        setVideo()
        setFullscreen()
        initCamera()
        initCollect()

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    popBack()
                }
            })
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectUserKeyPoints()
            collectTeacherKeyPoints()
        }
    }

    private fun CoroutineScope.collectUserKeyPoints() = launch {
        viewModel.userKeyPoints.collectLatest { keyPoints ->
            binding.poseView.updateUserKeyPoints(keyPoints)
        }
    }

    private fun CoroutineScope.collectTeacherKeyPoints() = launch {
        viewModel.teacherKeyPoints.collectLatest { keyPoints ->
            binding.poseView.updateTeacherKeyPoints(keyPoints)
        }
    }

    private fun setVideo() = with(binding) {
        player = ExoPlayer.Builder(requireContext()).build()
        val mediaItems = args.uriList.map { url ->
            MediaItem.fromUri(url)
        }

        player.setMediaItems(mediaItems)
        player.prepare()

        pvVideo.player = player
        pvVideo.setBackgroundColor(Color.BLACK)
    }

    private fun setFullscreen() = with(requireActivity() as MainActivity) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        supportActionBar?.hide()
    }

    private fun exitFullscreen() = with(requireActivity() as MainActivity) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
        supportActionBar?.show()
    }

    private fun popBack() {
        exitFullscreen()
        findNavController().popBackStack()
    }

    private fun initCamera() {
        val cameraProvider = ProcessCameraProvider
            .getInstance(requireContext())
            .get()

        val cameraSelector = CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        val resolutionSelector = ResolutionSelector
            .Builder()
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
            .build()

        val analysis = ImageAnalysis
            .Builder()
            .setResolutionSelector(resolutionSelector)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    viewModel.inferImage(imageProxy, width, height)
                    viewModel.inferVideo(player, width, height)
                }
            }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, analysis)
    }
}
