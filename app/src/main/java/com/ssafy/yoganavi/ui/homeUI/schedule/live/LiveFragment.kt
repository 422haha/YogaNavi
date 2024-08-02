package com.ssafy.yoganavi.ui.homeUI.schedule.live

import android.Manifest
import android.os.Bundle
import android.view.View
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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _: Boolean -> }

    private val permissionHandler: PermissionHandler by lazy {
        PermissionHandler(
            requireActivity(),
            requestPermissionLauncher
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(false, "", false)

        initListener()

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
                viewModel.sessionManager.flipCamera()
            }

            ibtnCancel.setOnClickListener {
                viewModel.sessionManager.disconnect()
                findNavController().popBackStack()
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
            WebRTCSessionState.Active -> { observeCallMediaState() }
            WebRTCSessionState.Ready -> { }
            WebRTCSessionState.Creating -> { }
            WebRTCSessionState.Impossible,
            WebRTCSessionState.Offline -> {
                callMediaStateJob?.cancel()
                callMediaStateJob = null
            }
        }
    }

    private fun observeCallMediaState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.callMediaState.collect { state ->
                        handleMicrophoneState(state.isMicrophoneEnabled)
                        handleCameraState(state.isCameraEnabled)
                    }
            }
        }
    }

    private fun handleMicrophoneState(isEnabled: Boolean) {
        if(isEnabled) permissionHandler.branchPermission(Manifest.permission.RECORD_AUDIO, "오디오")

        viewModel.sessionManager.enableMicrophone(isEnabled)

        binding.ibtnMic.setImageResource(if (isEnabled) R.drawable.baseline_mic_24 else R.drawable.baseline_mic_off_24)
    }

    private fun handleCameraState(isEnabled: Boolean) {
        if(isEnabled) PermissionHelper(this, arrayOf(Manifest.permission.CAMERA), ::popBack).launchPermission()

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
