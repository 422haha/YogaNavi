package com.ssafy.yoganavi.ui.homeUI.schedule.live

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.databinding.FragmentLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.WebRTCSessionState
import com.ssafy.yoganavi.ui.utils.PermissionHandler
import com.ssafy.yoganavi.ui.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LiveFragment: BaseFragment<FragmentLiveBinding>(FragmentLiveBinding::inflate) {

    private val viewModel: LiveViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _: Boolean -> }

    private val permissionHandler: PermissionHandler by lazy { PermissionHandler(requireContext(), requestPermissionLauncher) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(false, "", false)

        checkPermission()

        initListener()

        setUI()

        observeSessionState()
    }

    private fun checkPermission() {
        PermissionHelper(this, arrayOf(Manifest.permission.CAMERA), ::popBack)
            .launchPermission()

        permissionHandler.branchPermission(Manifest.permission.RECORD_AUDIO, "오디오")
    }

    private fun initListener() {
        with(binding) {
            // 마이크 켜고 끄기
            ibtnMic.setOnClickListener {

            }

            // 비디오 일시 중지
            ibtnVideo.setOnClickListener {

            }

            // 전후면 카메라 전환
            ibtnCamSwitch.setOnClickListener {

            }

            ibtnCancel.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun observeSessionState() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.sessionManager.signalingClient.sessionStateFlow.collect { state ->
                updateUI(state)
            }
        }
    }

    private fun setUI() {
        binding.stageScreen.isVisible = true
        binding.videoCallScreen.isVisible = false
    }

    private fun updateUI(state: WebRTCSessionState) {
        if (state == WebRTCSessionState.Active || state == WebRTCSessionState.Ready) {
            binding.stageScreen.isVisible = false
            binding.videoCallScreen.isVisible = true
        } else {
            binding.stageScreen.isVisible = true
            binding.videoCallScreen.isVisible = false
        }
    }

    private fun popBack() {
        findNavController().popBackStack()
    }
}
