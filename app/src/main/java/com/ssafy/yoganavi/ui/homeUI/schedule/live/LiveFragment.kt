package com.ssafy.yoganavi.ui.homeUI.schedule.live

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.WebRTCSessionState
import com.ssafy.yoganavi.ui.utils.PermissionHandler
import com.ssafy.yoganavi.ui.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.webrtc.android.ui.VideoTextureViewRenderer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.webrtc.RendererCommon
import org.webrtc.VideoTrack

@AndroidEntryPoint
class LiveFragment : BaseFragment<FragmentLiveBinding>(FragmentLiveBinding::inflate) {

    private val viewModel: LiveViewModel by viewModels()
    private var callMediaStateJob: Job? = null

    private lateinit var localRenderer: VideoTextureViewRenderer
    private lateinit var remoteRenderer: VideoTextureViewRenderer

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var permissionHelper: PermissionHelper

    private lateinit var draggableContainer: FrameLayout

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

        setToolbar(false, "", false)

        initListener()

        initInMoveLocalView()

        observeCallMediaState()

        observeSessionState()

        renderInit()
    }

    private fun initListener() {
        with(binding) {
            ibtnMic.setOnClickListener {
                viewModel.toggleMicrophoneState(!viewModel.callMediaState.value.isMicrophoneEnabled)
            }

            ibtnVideo.setOnClickListener {
                viewModel.toggleCameraState(!viewModel.callMediaState.value.isCameraEnabled)
            }

            ibtnCamSwitch.setOnClickListener {
                if(viewModel.sessionState.value == WebRTCSessionState.Active)
                    viewModel.sessionManager.flipCamera()
            }

            ibtnCancel.setOnClickListener {
                findNavController().popBackStack()
            }
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
        callMediaStateJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sessionState.collect { state ->
                    handleSessionState(state)
                }
            }
        }
    }

    private fun handleSessionState(state: WebRTCSessionState) {
        binding.tvState.text = state.toString()

        when (state) {
            WebRTCSessionState.Active -> { }
            WebRTCSessionState.Ready -> { }
            WebRTCSessionState.Creating -> { }
            WebRTCSessionState.Impossible,
            WebRTCSessionState.Offline -> { }
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
        if(isEnabled) permissionHandler.branchPermission(Manifest.permission.RECORD_AUDIO, "오디오")

        if(viewModel.sessionState.value == WebRTCSessionState.Active)
            viewModel.sessionManager.enableMicrophone(isEnabled)

        binding.ibtnMic.setImageResource(if (isEnabled) R.drawable.baseline_mic_24 else R.drawable.baseline_mic_off_24)
    }

    private fun handleCameraState(isEnabled: Boolean) {
        if(isEnabled) permissionHelper.launchPermission()

        if(viewModel.sessionState.value == WebRTCSessionState.Active)
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
                    viewModel.sessionManager.localVideoTrackFlow.collectLatest {
                        cleanLocalTrack(it)
                        setupLocalVideo(it)
                    }
                }

                launch {
                    viewModel.sessionManager.remoteVideoTrackFlow.collectLatest {
                        cleanRemoteTrack(it)
                        setupRemoteVideo(it)
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

    private fun popBack() {
        findNavController().popBackStack()
    }
}