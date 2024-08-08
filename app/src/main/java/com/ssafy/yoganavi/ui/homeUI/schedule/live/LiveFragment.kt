package com.ssafy.yoganavi.ui.homeUI.schedule.live

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.core.MainActivity
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.WebRTCSessionState
import com.ssafy.yoganavi.ui.utils.NO_CONNECT_SERVER
import com.ssafy.yoganavi.ui.utils.PermissionHandler
import com.ssafy.yoganavi.ui.utils.PermissionHelper
import com.ssafy.yoganavi.ui.utils.WAIT_BROADCAST
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.webrtc.android.ui.VideoTextureViewRenderer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.webrtc.RendererCommon
import org.webrtc.VideoTrack

@AndroidEntryPoint
class LiveFragment : BaseFragment<FragmentLiveBinding>(FragmentLiveBinding::inflate) {
    private val args: LiveFragmentArgs by navArgs()

    private val viewModel: LiveViewModel by viewModels()

    private lateinit var localRenderer: VideoTextureViewRenderer
    private lateinit var remoteRenderer: VideoTextureViewRenderer

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var permissionHelper: PermissionHelper

    private lateinit var draggableContainer: FrameLayout

    private var prevState: WebRTCSessionState = WebRTCSessionState.Offline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPermission()
    }

    private fun initPermission() {
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { _: Boolean -> }

        permissionHandler = PermissionHandler(
            requireActivity(),
            requestPermissionLauncher
        )

        permissionHelper = PermissionHelper(this, arrayOf(Manifest.permission.CAMERA), ::popBack).apply {
            launchPermission()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.sessionManager.signalingClient.updateLiveId(args.getLiveId)

        setToolbar(false, "", false)

        initListener()

        setFullscreen()

        initInMoveLocalView()

        observeSessionState()

        observeCallMediaState()

        renderInit()
    }

    private fun initListener() {
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    popBack()
                }
            })

        with(binding) {
            ibtnMic.setOnClickListener {
                viewModel.toggleMicrophoneState(viewModel.callMediaState.value.isMicrophoneEnabled.not())
            }

            ibtnVideo.setOnClickListener {
                viewModel.toggleCameraState(viewModel.callMediaState.value.isCameraEnabled.not())
            }

            ibtnCamSwitch.setOnClickListener {
                if(viewModel.sessionManager.signalingClient.sessionStateFlow.value == WebRTCSessionState.Active)
                    viewModel.sessionManager.flipCamera()
            }

            ibtnCancel.setOnClickListener { popBack() }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initOutMoveLocalView() {
        draggableContainer = binding.draggableContainer
        draggableContainer.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.updateOffset(view.x - event.rawX, view.y - event.rawY)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + viewModel.offsetX.value)
                        .y(event.rawY + viewModel.offsetY.value)
                        .setDuration(0)
                        .start()
                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initInMoveLocalView() {
        draggableContainer = binding.draggableContainer
        draggableContainer.setOnTouchListener { view, event ->
            val parent = view.parent as View
            val parentWidth = parent.width
            val parentHeight = parent.height

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.updateOffset(view.x - event.rawX, view.y - event.rawY)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX + viewModel.offsetX.value
                    val newY = event.rawY + viewModel.offsetY.value

                    val clampedX = newX.coerceIn(0f, parentWidth - view.width.toFloat())
                    val clampedY = newY.coerceIn(0f, parentHeight - view.height.toFloat())

                    view.animate()
                        .x(clampedX)
                        .y(clampedY)
                        .setDuration(0)
                        .start()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeSessionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sessionManager.signalingClient.sessionStateFlow.collect { state ->
                    handleSessionState(state)
                }
            }
        }
    }

    private fun handleSessionState(state: WebRTCSessionState) {
        binding.tvState.text = state.name

        when (state) {
            WebRTCSessionState.Offline -> {
                binding.tvState.text = NO_CONNECT_SERVER

                if(prevState != WebRTCSessionState.Offline)
                    viewModel.sessionManager.disconnect()
            }
            WebRTCSessionState.Impossible -> {
                if(!args.isTeacher)
                    binding.tvState.text = WAIT_BROADCAST
            }
            WebRTCSessionState.Ready -> {
                if (args.isTeacher)
                    viewModel.sessionManager.onSessionScreenReady()
            }
            WebRTCSessionState.Creating -> {
                if(!args.isTeacher)
                    viewModel.sessionManager.onSessionScreenReady()
            }
            WebRTCSessionState.Active -> { }
        }

        prevState = state
    }

    private fun observeCallMediaState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.callMediaState.collectLatest { state ->
                        handleMicrophoneState(state.isMicrophoneEnabled)
                        handleCameraState(state.isCameraEnabled)
                }
            }
        }
    }

    private fun handleMicrophoneState(isEnabled: Boolean) {
        if(isEnabled) permissionHandler.branchPermission(Manifest.permission.RECORD_AUDIO, "오디오")

        if(viewModel.sessionManager.signalingClient.sessionStateFlow.value == WebRTCSessionState.Active)
            viewModel.sessionManager.enableMicrophone(isEnabled)

        binding.ibtnMic.setImageResource(if (isEnabled) R.drawable.baseline_mic_24 else R.drawable.baseline_mic_off_24)
    }

    private fun handleCameraState(isEnabled: Boolean) {
        if(isEnabled) permissionHelper.launchPermission()

        if(viewModel.sessionManager.signalingClient.sessionStateFlow.value == WebRTCSessionState.Active)
            viewModel.sessionManager.enableCamera(isEnabled)

        binding.ibtnVideo.setImageResource(if (isEnabled) R.drawable.baseline_videocam_24 else R.drawable.baseline_videocam_off_24)
    }

    private fun renderInit() {
        localRenderer = binding.localVideoCallScreen
        remoteRenderer = binding.remoteVideoCallScreen

        localRenderer.init(viewModel.sessionManager.peerConnectionFactory.eglBaseContext,
            object: RendererCommon.RendererEvents {
                override fun onFirstFrameRendered() = Unit

                override fun onFrameResolutionChanged(width: Int, height: Int, rotation: Int) = Unit
            })

        remoteRenderer.init(viewModel.sessionManager.peerConnectionFactory.eglBaseContext,
            object: RendererCommon.RendererEvents {
                override fun onFirstFrameRendered() = Unit

                override fun onFrameResolutionChanged(width: Int, height: Int, rotation: Int) = Unit
            })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.sessionManager.localVideoTrackFlow.collectLatest { videoTrack ->
                        cleanLocalTrack(videoTrack)
                        setupLocalVideo(videoTrack)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.sessionManager.remoteVideoTrackFlow.collectLatest { videoTrack ->
                        cleanRemoteTrack(videoTrack)
                        setupRemoteVideo(videoTrack)
                    }
                }
            }
        }
    }

    private fun setupLocalVideo(videoTrack: VideoTrack?) {
        videoTrack?.addSink(localRenderer)
    }

    private fun setupRemoteVideo(videoTrack: VideoTrack?) {
        videoTrack?.addSink(remoteRenderer)
    }

    private fun cleanLocalTrack(videoTrack: VideoTrack?) {
        videoTrack?.removeSink(localRenderer)
    }

    private fun cleanRemoteTrack(videoTrack: VideoTrack?) {
        videoTrack?.removeSink(remoteRenderer)
    }

    private fun setFullscreen() = with(requireActivity() as MainActivity) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

        runCatching { viewModel.sessionManager.disconnect() }

        findNavController().popBackStack()
    }
}