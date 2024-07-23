package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.lecture.VideoChapterData
import com.ssafy.yoganavi.databinding.FragmentRegisterVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.ChapterAdapter
import com.ssafy.yoganavi.ui.utils.CREATE
import com.ssafy.yoganavi.ui.utils.REGISTER_VIDEO
import com.ssafy.yoganavi.ui.utils.SAVE
import com.ssafy.yoganavi.ui.utils.UPDATE
import com.ssafy.yoganavi.ui.utils.getImagePath
import com.ssafy.yoganavi.ui.utils.getVideoPath
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
                val imageUri = result.data?.data ?: return@registerForActivityResult
                val imagePath = getImagePath(requireContext(), imageUri)
                if (imagePath.isNotBlank()) {
                    val uri = Uri.parse(imagePath)
                    binding.ivVideo.setImageURI(uri)
                    binding.tvAddThumbnail.visibility = View.GONE
                    viewModel.setThumbnail(path = imagePath)
                }
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
        setToolbar(
            isBottomNavigationVisible = false,
            title = REGISTER_VIDEO,
            canGoBack = true,
            menuItem = if (args.recordedId != -1L) UPDATE else CREATE,
            menuListener = ::makeLecture
        )

        binding.rvLecture.adapter = chapterAdapter
        initCollect()
        initListener()

        if (args.recordedId != -1L) viewModel.getLecture(args.recordedId) { data ->
            setView(data)
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.chapterList.collectLatest { list ->
                chapterAdapter.submitList(list)
                viewModel.setChapterList(list)
            }
        }
    }

    private fun initListener() {
        binding.btnAddChapter.setOnClickListener { addChapter() }
        binding.ivVideo.setOnClickListener { addThumbnail() }
    }

    private suspend fun setView(data: LectureDetailData) = withContext(Dispatchers.Main) {
        with(binding) {
            etTitle.setText(data.recordTitle)
            etContent.setText(data.recordContent)

            val circularProgressDrawable = CircularProgressDrawable(requireContext()).apply {
                strokeWidth = 5f
                centerRadius = 30f
            }
            circularProgressDrawable.start()

            if (data.recordThumbnail.isNotBlank()) {
                Glide.with(requireContext())
                    .load(data.recordThumbnail)
                    .placeholder(circularProgressDrawable)
                    .into(ivVideo)

                tvAddThumbnail.visibility = View.GONE
            }
        }
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
            val realPath = getVideoPath(requireContext(), uri)
            if (realPath.isNotBlank()) viewModel.setVideo(data, realPath)
        }

        videoUriLauncher.launch(intent)
    }

    private fun makeLecture() = with(binding) {
        val title = etTitle.text.toString()
        val content = etContent.text.toString()
        val chapterTitleList = mutableListOf<String>()
        val chapterContentList = mutableListOf<String>()

        for (index in 0 until rvLecture.childCount) {
            val itemView = rvLecture.getChildAt(index)
            val chapterTitleView = itemView.findViewById<EditText>(R.id.et_title)
            val chapterContentView = itemView.findViewById<EditText>(R.id.et_content)

            chapterTitleList.add(chapterTitleView.text.toString())
            chapterContentList.add(chapterContentView.text.toString())
        }

        viewModel.sendLecture(
            id = args.recordedId,
            lectureTitle = title,
            lectureContent = content,
            titleList = chapterTitleList,
            contentList = chapterContentList,
            onSuccess = ::successToUpload,
            onFailure = ::failToUpload
        )
    }

    private suspend fun successToUpload() = withContext(Dispatchers.Main) {
        showSnackBar(SAVE)
        findNavController().popBackStack()
    }

    private suspend fun failToUpload(message: String) = withContext(Dispatchers.Main) {
        showSnackBar(message)
    }

    private fun addChapter() = viewModel.addChapter()
    private fun deleteChapter(data: VideoChapterData) = viewModel.deleteChapter(data)
}
