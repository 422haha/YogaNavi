package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.databinding.FragmentLectureVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.core.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LectureVideoFragment : BaseFragment<FragmentLectureVideoBinding>(
    FragmentLectureVideoBinding::inflate
) {
    private val args by navArgs<LectureVideoFragmentArgs>()
    private val viewModel: LectureVideoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVideo()
        setFullscreen()

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitFullscreen()
                    findNavController().popBackStack()
                }
            })
    }

    private fun setVideo() = with(binding) {
        val player = ExoPlayer.Builder(requireContext()).build()
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

}
