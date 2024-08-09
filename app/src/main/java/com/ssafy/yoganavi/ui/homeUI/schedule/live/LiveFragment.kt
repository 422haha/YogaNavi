package com.ssafy.yoganavi.ui.homeUI.schedule.live

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context.AUDIO_SERVICE
import android.content.pm.ActivityInfo
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
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
import com.ssafy.yoganavi.ui.utils.PermissionHelper
import com.ssafy.yoganavi.ui.utils.WAIT_BROADCAST
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.webrtc.android.ui.VideoTextureViewRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
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

    private lateinit var permissionHelper: PermissionHelper

    private lateinit var draggableContainer: FrameLayout

    private var prevState: WebRTCSessionState = WebRTCSessionState.Offline
    private var isMirrorMode = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPermission()

        viewModel.sessionManager.signalingClient.updateLiveId(args.getLiveId)

        setToolbar(false, "", false)

        setSpeakerphoneOn(true)

        initListener()

        setFullscreen()

        initInMoveLocalView()

        renderInit()

        observeSessionState()

        observeCallMediaState()
    }

    private fun isPermission() {
        viewModel.sessionManager.onLocalScreen()
    }

    private fun initPermission() {
        permissionHelper =
            PermissionHelper(this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                ::popBack,
                ::isPermission).apply {
                launchPermission()
            }
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
                if (viewModel.sessionManager.signalingClient.sessionStateFlow.value == WebRTCSessionState.Active) {
                    isMirrorMode = !isMirrorMode
                    localVideoCallScreen.setMirror(isMirrorMode)

                    viewModel.sessionManager.flipCamera()
                }
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
        binding.localVideoCallScreen.setMirror(isMirrorMode)

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
        if(state == WebRTCSessionState.Ready || state == WebRTCSessionState.Creating)
            onBuffering(true)
        else if(state == WebRTCSessionState.Offline || state == WebRTCSessionState.Impossible || state == WebRTCSessionState.Active)
            onBuffering(false)

        when (state) {
            WebRTCSessionState.Offline -> {
                if (prevState != WebRTCSessionState.Offline) {
                    runCatching  { viewModel.sessionManager.disconnect() }
                        .onFailure { it.printStackTrace() }
                }
            }

            WebRTCSessionState.Impossible -> {
                if(prevState == WebRTCSessionState.Active)
                    renderInitWhenImpossible()
            }

            WebRTCSessionState.Ready -> {
                if (args.isTeacher)
                    viewModel.sessionManager.onSessionReady()
            }

            WebRTCSessionState.Creating -> {
                if (!args.isTeacher)
                    viewModel.sessionManager.onSessionReady()
            }

            WebRTCSessionState.Active -> { setBackLocalScreenSize() }
        }

        prevState = state
    }

    private fun renderInitWhenImpossible() {
        val layoutParams = binding.draggableContainer.layoutParams

        val startWidth = layoutParams.width
        val startHeight = layoutParams.height

        val parentWidth = (binding.draggableContainer.parent as View).width
        val parentHeight = (binding.draggableContainer.parent as View).height

        val widthAnimator = ValueAnimator.ofInt(startWidth, parentWidth).apply {
            addUpdateListener { animator ->
                layoutParams.width = animator.animatedValue as Int
                binding.draggableContainer.layoutParams = layoutParams
            }
        }

        val heightAnimator = ValueAnimator.ofInt(startHeight, parentHeight).apply {
            addUpdateListener { animator ->
                layoutParams.height = animator.animatedValue as Int
                binding.draggableContainer.layoutParams = layoutParams
            }
        }

        val animatorSet = AnimatorSet().apply {
            playTogether(widthAnimator, heightAnimator)
        }

        animatorSet.start()
    }

    private fun setBackLocalScreenSize() {
        val layoutParams = binding.draggableContainer.layoutParams

        val startWidth = binding.root.width
        val startHeight = binding.root.height
        val endWidth = 525
        val endHeight = 394

        val widthAnimator = ValueAnimator.ofInt(startWidth, endWidth).apply {
            addUpdateListener { animator ->
                layoutParams.width = animator.animatedValue as Int
                binding.draggableContainer.layoutParams = layoutParams
            }
        }

        val heightAnimator = ValueAnimator.ofInt(startHeight, endHeight).apply {
            addUpdateListener { animator ->
                layoutParams.height = animator.animatedValue as Int
                binding.draggableContainer.layoutParams = layoutParams
            }
        }

        val animatorSet = AnimatorSet().apply {
            duration = 300
            playTogether(widthAnimator, heightAnimator)
        }

        animatorSet.start()
    }

    private fun onBuffering(isEnabled: Boolean) {
        if(isEnabled) {
            binding.lav.playAnimation()
            binding.lav.isVisible = true
        } else {
            binding.lav.pauseAnimation()
            binding.lav.isVisible = false
        }
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
        runCatching {
            if (viewModel.sessionManager.signalingClient.sessionStateFlow.value == WebRTCSessionState.Active)
                viewModel.sessionManager.enableMicrophone(isEnabled)
        }.onSuccess {
            binding.ibtnMic.setImageResource(if (isEnabled) R.drawable.baseline_mic_24 else R.drawable.baseline_mic_off_24)
        }
    }

    private fun handleCameraState(isEnabled: Boolean) {
        runCatching {
            if (viewModel.sessionManager.signalingClient.sessionStateFlow.value == WebRTCSessionState.Active)
                viewModel.sessionManager.enableCamera(isEnabled)
        }.onSuccess {
            binding.ibtnVideo.setImageResource(if (isEnabled) R.drawable.baseline_videocam_24 else R.drawable.baseline_videocam_off_24)
        }
    }

    private fun renderInit() {
        localRenderer = binding.localVideoCallScreen
        remoteRenderer = binding.remoteVideoCallScreen

        localRenderer.init(viewModel.sessionManager.peerConnectionFactory.eglBaseContext,
            object : RendererCommon.RendererEvents {
                override fun onFirstFrameRendered() = Unit

                override fun onFrameResolutionChanged(width: Int, height: Int, rotation: Int) = Unit
            })

        remoteRenderer.init(viewModel.sessionManager.peerConnectionFactory.eglBaseContext,
            object : RendererCommon.RendererEvents {
                override fun onFirstFrameRendered() = Unit

                override fun onFrameResolutionChanged(width: Int, height: Int, rotation: Int) = Unit
            })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collectVideoTrack(viewModel.sessionManager.localVideoTrackFlow, localRenderer)
                collectVideoTrack(viewModel.sessionManager.remoteVideoTrackFlow, remoteRenderer)
            }
        }
    }

    private fun CoroutineScope.collectVideoTrack(flow: Flow<VideoTrack?>, renderer: VideoTextureViewRenderer) = launch {
        flow.collectLatest { videoTrack ->
            runCatching {
                setupVideoTrack(videoTrack, renderer)
            }
        }
    }

    private fun cleanVideoTrack(videoTrack: VideoTrack?, renderer: VideoTextureViewRenderer) {
        videoTrack?.removeSink(renderer)
    }

    private fun setupVideoTrack(videoTrack: VideoTrack?, renderer: VideoTextureViewRenderer) {
        cleanVideoTrack(videoTrack, renderer)

        videoTrack?.addSink(renderer)
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

    private fun setSpeakerphoneOn(on: Boolean) {
        val audioManager =  requireActivity().getSystemService(AUDIO_SERVICE) as AudioManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val devices = audioManager.availableCommunicationDevices
            val speakerDevice = devices.find { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }

            if (on && speakerDevice != null) {
                audioManager.setCommunicationDevice(speakerDevice)
            } else {
                audioManager.clearCommunicationDevice()
            }
        } else {
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            @Suppress("DEPRECATION")
            audioManager.isSpeakerphoneOn = on
        }
    }

    private fun popBack() {
        runCatching {
            onBuffering(true)
            viewModel.sessionManager.disconnect()
        }.also {
            onBuffering(false)
            setSpeakerphoneOn(false)
            exitFullscreen()
            findNavController().popBackStack()
        }
    }
}