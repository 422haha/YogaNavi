package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.ssafy.yoganavi.databinding.FragmentRegisterVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.adapter.ChapterAdapter
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.viewHolder.VideoViewHolder
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
import kotlin.math.abs

@AndroidEntryPoint
class RegisterVideoFragment : BaseFragment<FragmentRegisterVideoBinding>(
    FragmentRegisterVideoBinding::inflate
) {
    private val args by navArgs<RegisterVideoFragmentArgs>()
    private val viewModel: RegisterVideoViewModel by viewModels()
    private val exoPlayer: ExoPlayer by lazy { ExoPlayer.Builder(requireContext()).build() }
    private var prevPosition = RecyclerView.NO_POSITION
    private var currentPosition = RecyclerView.NO_POSITION
    private var handleVideoResult: ((Uri) -> Unit)? = null
    private val chapterAdapter by lazy {
        ChapterAdapter(
            exoPlayer = exoPlayer,
            addImage = ::addThumbnail,
            addVideoListener = ::addVideo,
            deleteListener = ::deleteChapter,
            changeThumbnailTitle = ::changeThumbnailTitle,
            changeThumbnailContent = ::changeThumbnailContent,
            changeVideoTitle = ::changeVideoTitle,
            changeVideoContent = ::changeVideoContent,
            loadS3ImageSequentially = ::loadS3ImageSequentially,
            loadS3VideoFrame = ::loadS3VideoFrame,
            makeUrlFromKey = ::makeUrlFromKey
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
        rvLecture.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val newCenterPosition = findCenterItemPosition(manager)

                if (newCenterPosition != currentPosition) {
                    prevPosition = currentPosition
                    currentPosition = newCenterPosition
                    handleCenterItem()
                }
            }
        })
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

    private fun findCenterItemPosition(layoutManager: LinearLayoutManager): Int {
        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        val lastPosition = layoutManager.findLastVisibleItemPosition()

        var closestPosition = RecyclerView.NO_POSITION
        var closestDistance = Int.MAX_VALUE

        for (i in firstPosition..lastPosition) {
            val viewHolder = binding.rvLecture.findViewHolderForAdapterPosition(i) ?: continue
            val itemView = viewHolder.itemView

            val itemCenterY = (itemView.top + itemView.bottom) / 2
            val screenCenterY = binding.rvLecture.height / 2

            val distance = abs(itemCenterY - screenCenterY)
            if (distance < closestDistance) {
                closestDistance = distance
                closestPosition = i
            }
        }

        return closestPosition
    }

    private fun handleCenterItem() {
        stopVideoAboutPrevCenterItem()
        startVideoAboutCurrentCenterItem()
    }

    private fun stopVideoAboutPrevCenterItem() {
        if (prevPosition == RecyclerView.NO_POSITION) return
        val prevViewHolder = binding.rvLecture.findViewHolderForAdapterPosition(prevPosition)
                as? VideoViewHolder ?: return

        prevViewHolder.removeExoPlayer()
    }

    private fun startVideoAboutCurrentCenterItem() {
        if (currentPosition == RecyclerView.NO_POSITION) return
        val currentViewHolder = binding.rvLecture.findViewHolderForAdapterPosition(currentPosition)
                as? VideoViewHolder ?: return

        currentViewHolder.getVideo()
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

    private fun loadS3ImageSequentially(view: ImageView, smallKey: String, largeKey: String) =
        viewModel.loadS3ImageSequentially(view, smallKey, largeKey)

    private fun loadS3VideoFrame(view: ImageView, key: String, time: Long, isCircularOn: Boolean) =
        viewModel.loadS3VideoFrame(view, key, time, isCircularOn)

    private fun makeUrlFromKey(key: String): String = viewModel.makeUrlFromKey(key)

    private fun makeLecture() = viewModel.makeLecture(
        args.recordedId,
        ::loadingView,
        ::successToUpload,
        ::failToUpload
    )

    private fun addChapter() = viewModel.addChapter()
    private fun deleteChapter(position: Int) = viewModel.deleteChapter(position)


    private suspend fun successToUpload() = withContext(Dispatchers.Main) {
        findNavController().popBackStack()
    }

    private suspend fun loadingView() = withContext(Dispatchers.Main) {
        binding.vBg.visibility = View.VISIBLE
        binding.lav.visibility = View.VISIBLE
    }

    private suspend fun failToUpload(message: String) = withContext(Dispatchers.Main) {
        binding.vBg.visibility = View.GONE
        binding.lav.visibility = View.GONE
        showSnackBar(message)
    }
}
