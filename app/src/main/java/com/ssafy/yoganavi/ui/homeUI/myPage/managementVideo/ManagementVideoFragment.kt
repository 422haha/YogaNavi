package com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.home.EmptyData
import com.ssafy.yoganavi.databinding.FragmentManagementVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo.lecture.LectureAdapter
import com.ssafy.yoganavi.ui.utils.DELETE
import com.ssafy.yoganavi.ui.utils.EDIT
import com.ssafy.yoganavi.ui.utils.EMPTY_MY_LECTURE
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_VIDEO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManagementVideoFragment : BaseFragment<FragmentManagementVideoBinding>(
    FragmentManagementVideoBinding::inflate
) {
    private val viewModel: ManagementVideoViewModel by viewModels()
    private val lectureAdapter by lazy {
        LectureAdapter(
            navigateToRegisterVideoFragment = ::navigateToRegisterVideoFragment,
            sendLikeLecture = ::sendLikeLecture
        )
    }
    private var isDeleteMode = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(
            isBottomNavigationVisible = false,
            title = MANAGEMENT_VIDEO,
            canGoBack = true,
            menuItem = EDIT,
            menuListener = ::setMode
        )

        binding.rvLecture.adapter = lectureAdapter
        initListener()
        initCollect()
        viewModel.getLectureList()
    }

    private fun initListener() {
        binding.fabEdit.setOnClickListener {
            navigateToRegisterVideoFragment()
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.lectureList.collectLatest {
                checkEmptyList(it, EmptyData(EMPTY_MY_LECTURE))
                lectureAdapter.submitList(it)
            }
        }
    }

    private fun setMode() {
        isDeleteMode = !isDeleteMode

        if (isDeleteMode) {
            changeToolbar(DELETE)
            setSelectListener()
        } else {
            changeToolbar(EDIT)
            setDeleteListener()
        }
    }

    private fun setDeleteListener() = with(binding.rvLecture) {
        val indexList = mutableListOf<Int>()
        for (idx in 0 until childCount) {
            val childView = getChildAt(idx)
            val deleteBtn = childView.findViewById<RadioButton>(R.id.rb_delete)
            val thumbnailView = childView.findViewById<ImageView>(R.id.iv_thumbnail)
            val likeView = childView.findViewById<ImageView>(R.id.iv_favorite)

            if (deleteBtn.isChecked) {
                indexList.add(idx)
            }

            thumbnailView.isClickable = true
            likeView.isClickable = true
            childView.isClickable = false
            deleteBtn.isChecked = false
            deleteBtn.visibility = View.GONE
        }
        if (indexList.isNotEmpty()) viewModel.deleteLecture(indexList)
    }

    private fun setSelectListener() = with(binding.rvLecture) {
        for (idx in 0 until childCount) {
            val childView = getChildAt(idx)
            val deleteBtn = childView.findViewById<RadioButton>(R.id.rb_delete)
            val thumbnailView = childView.findViewById<ImageView>(R.id.iv_thumbnail)
            val likeView = childView.findViewById<ImageView>(R.id.iv_favorite)

            thumbnailView.isClickable = false
            likeView.isClickable = false
            childView.isClickable = true
            deleteBtn.visibility = View.VISIBLE

            childView.setOnClickListener {
                deleteBtn.isChecked = !deleteBtn.isChecked
            }
        }
    }

    private fun changeToolbar(menu: String) = setToolbar(
        isBottomNavigationVisible = false,
        title = MANAGEMENT_VIDEO,
        canGoBack = true,
        menuItem = menu,
        menuListener = ::setMode
    )

    private fun sendLikeLecture(recordedId: Long) = viewModel.setLectureLike(recordedId)

    private fun navigateToRegisterVideoFragment(recordedId: Long = -1L) {
        val directions = ManagementVideoFragmentDirections
            .actionManagementVideoFragmentToRegisterVideoFragment(recordedId)

        findNavController().navigate(directions)
    }
}
