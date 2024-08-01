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
import com.ssafy.yoganavi.databinding.FragmentLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.PermissionHandler
import com.ssafy.yoganavi.ui.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.webrtc.android.ui.VideoTextureViewRenderer
import kotlinx.coroutines.launch
import org.webrtc.RendererCommon
import org.webrtc.VideoTrack

@AndroidEntryPoint
class LiveFragment: BaseFragment<FragmentLiveBinding>(FragmentLiveBinding::inflate) {

    private val viewModel: LiveViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _: Boolean -> }

    private val permissionHandler: PermissionHandler by lazy { PermissionHandler(requireActivity(), requestPermissionLauncher) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(false, "", false)

        checkPermission()

        initListener()

        observeSessionState()
    }

    private fun initListener() {
        with(binding) {
            // 마이크 켜고 끄기
            ibtnMic.setOnClickListener {

            }

            // 비디오 일시 중지
            ibtnVideo.setOnClickListener {
                videoCallScreen.pauseVideo()
            }

            // 전후면 카메라 전환
            ibtnCamSwitch.setOnClickListener {
                videoCallScreen.setMirror(true)
            }

            ibtnCancel.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun observeSessionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.sessionManager.signalingClient.sessionStateFlow.collect { state ->
                    binding.tvState.text = state.toString()
                }
            }
        }
    }

    private fun checkPermission() {
        PermissionHelper(this, arrayOf(Manifest.permission.CAMERA), ::popBack)
            .launchPermission()

        permissionHandler.branchPermission(Manifest.permission.RECORD_AUDIO, "오디오")
    }

    private fun popBack() {
        findNavController().popBackStack()
    }

    fun VideoRenderer(videoTrack: VideoTrack) {
        val sessionManager = viewModel.sessionManager

        VideoTextureViewRenderer(requireActivity()).apply {
            init(
                sessionManager.peerConnectionFactory.eglBaseContext,
                object : RendererCommon.RendererEvents {
                    override fun onFirstFrameRendered() = Unit

                    override fun onFrameResolutionChanged(p0: Int, p1: Int, p2: Int) = Unit
                }
            )
            setupVideo(trackState, videoTrack, this)
            view = this
        }

        AndroidView(
            factory = { context ->

            },
            update = { v -> setupVideo(trackState, videoTrack, v) },
            modifier = modifier
        )
    }

    private fun cleanTrack(
        view: VideoTextureViewRenderer?,
        trackState: MutableState<VideoTrack?>
    ) {
        view?.let { trackState.value?.removeSink(it) }
        trackState.value = null
    }

    private fun setupVideo(
        trackState: MutableState<VideoTrack?>,
        track: VideoTrack,
        renderer: VideoTextureViewRenderer
    ) {
        if (trackState.value == track) {
            return
        }

        cleanTrack(renderer, trackState)

        trackState.value = track
        track.addSink(renderer)
    }
}
