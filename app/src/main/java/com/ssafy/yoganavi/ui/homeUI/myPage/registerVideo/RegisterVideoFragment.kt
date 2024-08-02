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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.databinding.FragmentRegisterVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.adapter.ChapterAdapter
import com.ssafy.yoganavi.ui.utils.CREATE
import com.ssafy.yoganavi.ui.utils.REGISTER_VIDEO
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
        ChapterAdapter(
            addImage = ::addThumbnail,
            addVideoListener = ::addVideo,
            deleteListener = ::deleteChapter,
            changeThumbnailTitle = ::changeThumbnailTitle,
            changeThumbnailContent = ::changeThumbnailContent,
            changeVideoTitle = ::changeVideoTitle,
            changeVideoContent = ::changeVideoContent
        )
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
            menuListener = ::makeLecture
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
            viewModel.chapterList.collectLatest { chapterItem ->
                chapterAdapter.submitList(chapterItem)
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
            if (realPath.isNotBlank()) viewModel.setVideo(position, realPath)
        }

        videoUriLauncher.launch(intent)
    }

    private fun changeThumbnailTitle(title: String) = viewModel.setThumbnailTitle(title)

    private fun changeThumbnailContent(content: String) = viewModel.setThumbnailContent(content)

    private fun changeVideoTitle(
        title: String,
        position: Int
    ) = viewModel.setVideoTitle(title, position)

    private fun changeVideoContent(
        content: String,
        position: Int
    ) = viewModel.setVideoContent(content, position)

    private fun makeLecture() = viewModel.makeLecture(
        args.recordedId,
        ::successToUpload,
        ::failToUpload
    )

    private fun addChapter() = viewModel.addChapter()
    private fun deleteChapter(position: Int) = viewModel.deleteChapter(position)

    private suspend fun successToUpload() = withContext(Dispatchers.Main) {
        findNavController().popBackStack()
    }

    private suspend fun failToUpload(message: String) = withContext(Dispatchers.Main) {
        showSnackBar(message)
    }
}
