package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.lecture.VideoChapterData
import com.ssafy.yoganavi.databinding.FragmentRegisterVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.ChapterAdapter
import com.ssafy.yoganavi.ui.utils.REGISTER_VIDEO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RegisterVideoFragment : BaseFragment<FragmentRegisterVideoBinding>(
    FragmentRegisterVideoBinding::inflate
) {
    private val args by navArgs<RegisterVideoFragmentArgs>()
    private val viewModel: RegisterVideoViewModel by viewModels()
    private val chapterAdapter by lazy { ChapterAdapter(::addVideo, ::deleteChapter) }
    private val imageUriLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                binding.ivVideo.setImageURI(imageUri)
                binding.tvAddThumbnail.visibility = View.GONE
            }
        }
    private val videoUriLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val videoUri = result.data?.data ?: return@registerForActivityResult
                handleVideoResult?.invoke(videoUri)
            }
        }
    private var handleVideoResult: ((Uri) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false,REGISTER_VIDEO,true)

        if (args.recordedId != -1) viewModel.getLecture(args.recordedId)
        binding.rvLecture.adapter = chapterAdapter
        initCollect()
        initListener()
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.lectureState.collectLatest {
                setView(it)
            }
        }
    }

    private fun initListener() {
        binding.btnAddChapter.setOnClickListener { addChapter() }
        binding.ivVideo.setOnClickListener { addThumbnail() }
    }

    private fun setView(data: LectureDetailData) = with(binding) {
        etContent.setText(data.recordTitle)
        etContent.setText(data.recordContent)
        chapterAdapter.submitList(data.recordedLectureChapters)
    }

    private fun addThumbnail() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        imageUriLauncher.launch(intent)
    }

    private fun addVideo(data: VideoChapterData) {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_GET_CONTENT
        }

        handleVideoResult = { uri ->
            viewModel.setVideo(data, uri.toString())
        }

        videoUriLauncher.launch(intent)
    }

    private fun addChapter() = viewModel.addChapter()
    private fun deleteChapter(data: VideoChapterData) = viewModel.deleteChapter(data)
}
