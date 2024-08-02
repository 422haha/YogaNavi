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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding
import com.ssafy.yoganavi.databinding.FragmentRegisterVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.ChapterAdapter
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.ChapterItem
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.ThumbnailData
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
    private var handleVideoResult: ((Uri) -> Unit)? = null
    private val chapterAdapter by lazy {
        ChapterAdapter(::addThumbnail, ::addVideo, ::deleteChapter, ::getVideo)
    }

    private val imageUriLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            val imageUri = result.data?.data ?: return@registerForActivityResult

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val (imagePath, miniPath) = getImagePath(requireContext(), imageUri)
                viewModel.setThumbnail(path = imagePath, miniPath = miniPath)
            }
        }

    private val videoUriLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            val videoUri = result.data?.data ?: return@registerForActivityResult

            handleVideoResult?.invoke(videoUri)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(
            isBottomNavigationVisible = false,
            title = REGISTER_VIDEO,
            canGoBack = true,
            menuItem = if (args.recordedId != -1L) UPDATE else CREATE,
            menuListener = { }
        )

        binding.rvLecture.adapter = chapterAdapter
        initCollect()
        initListener()
        if (args.recordedId != -1L) viewModel.getLecture(args.recordedId)
    }

    private fun initListener() = with(binding) {
        btnAddChapter.setOnClickListener { addChapter() }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.chapterList.collectLatest { list ->
                val itemList = mutableListOf<ChapterItem>()

                val thumbnailData = ThumbnailData(
                    recordedId = list.recordedId,
                    recordTitle = list.recordTitle,
                    recordContent = list.recordContent,
                    recordThumbnail = list.recordThumbnail,
                    recordThumbnailSmall = list.recordThumbnailSmall,
                    recordThumbnailPath = list.recordThumbnailPath,
                    miniThumbnailPath = list.miniThumbnailPath,
                    thumbnailKey = list.thumbnailKey,
                    miniThumbnailKey = list.miniThumbnailKey
                )
                itemList.add(ChapterItem.ImageItem(thumbnailData))

                list.recordedLectureChapters.forEach {
                    itemList.add(ChapterItem.VideoItem(it))
                }

                chapterAdapter.submitList(itemList)
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

    private fun addVideo(position: Int) {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_GET_CONTENT
        }

        handleVideoResult = { uri ->
            val realPath = getVideoPath(requireContext(), uri)
            if (realPath.isNotBlank()) viewModel.setVideo(position - 1, realPath)
        }

        videoUriLauncher.launch(intent)
    }

    private fun getVideo(
        url: String,
        binding: CustomChapterViewBinding
    ) = viewLifecycleOwner.lifecycleScope.launch {
        with(binding) {
            val mediaItem = MediaItem.fromUri(url)
            pvVideo.player = ExoPlayer.Builder(root.context).build().apply {
                setMediaItem(mediaItem)
            }
            pvVideo.player?.prepare()

            tvAddVideo.visibility = View.GONE
            grayView.visibility = View.GONE
            pvVideo.visibility = View.VISIBLE
        }
    }

    private fun makeLecture() = viewModel.makeLecture()

//    private fun makeLecture() = with(binding) {
//        val title = etTitle.text.toString()
//        val content = etContent.text.toString()
//        val chapterTitleList = mutableListOf<String>()
//        val chapterContentList = mutableListOf<String>()
//
//        for (index in 0 until rvLecture.childCount) {
//            val itemView = rvLecture.getChildAt(index)
//            val chapterTitleView = itemView.findViewById<EditText>(R.id.et_title)
//            val chapterContentView = itemView.findViewById<EditText>(R.id.et_content)
//
//            chapterTitleList.add(chapterTitleView.text.toString())
//            chapterContentList.add(chapterContentView.text.toString())
//        }
//
//        viewModel.sendLecture(
//            id = args.recordedId,
//            lectureTitle = title,
//            lectureContent = content,
//            titleList = chapterTitleList,
//            contentList = chapterContentList,
//            onSuccess = ::successToUpload,
//            onFailure = ::failToUpload
//        )
//    }

    private suspend fun successToUpload() = withContext(Dispatchers.Main) {
        showSnackBar(SAVE)
        findNavController().popBackStack()
    }

    private suspend fun failToUpload(message: String) = withContext(Dispatchers.Main) {
        showSnackBar(message)
    }

    private fun addChapter() = viewModel.addChapter()
    private fun deleteChapter(position: Int) = viewModel.deleteChapter(position - 1)

}
