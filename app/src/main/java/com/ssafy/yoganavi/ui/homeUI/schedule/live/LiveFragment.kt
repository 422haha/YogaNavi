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
import kotlinx.coroutines.launch
import timber.log.Timber

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

            }

            // 전후면 카메라 전환
            ibtnCamSwitch.setOnClickListener {

            }

            ibtnCancel.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun observeSessionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.sessionManager.signalingClient.sessionStateFlow.collect { state ->
                    Timber.d("상태 로그!! $state")
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
}
